package com.github.andreylitvintsev.profilefetcher

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.github.andreylitvintsev.profilefetcher.viewmodel.AuthViewModel
import com.github.andreylitvintsev.profilefetcher.viewmodel.observeWithErrorHandling
import kotlinx.android.synthetic.main.fragment_auth.*


class AuthFragment : Fragment(), OnNewIntentListener {

    companion object {
        const val CUSTOM_TAB_REQUEST_CODE = 777
        const val BUTTON_STATE_KEY = "buttonState"
    }

    private lateinit var newIntentListenerHolder: NewIntentListenerHolder
    private lateinit var detectAuthTokenListener: DetectAuthTokenListener

    private var nowLoadingAuthToken = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false).also {
            newIntentListenerHolder = (activity as? NewIntentListenerHolder)
                ?: error("${activity.toString()} must must implement NewIntentListenerHolder!")

            detectAuthTokenListener = (activity as? DetectAuthTokenListener)
                ?: error("${activity.toString()} must must implement DetectAuthTokenListener!")
        }
    }

    override fun onStart() {
        super.onStart()
        if (nowLoadingAuthToken) {
            signIn.isClickable = false
            signIn.displayedChild = 1
            tryRestoreAuthToken()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newIntentListenerHolder.addOnNewIntentListener(this)

        val customTabsIntent = CustomTabsIntent.Builder().build()
        signIn.setOnClickListener {
            it.isClickable = false
            customTabsIntent.launchUrlWithActivityResult(
                Uri.parse(
                    "https://github.com/login/oauth/authorize" +
                            "?client_id=d43b1ed587684df51150" +
                            "&scope=user" +
                            "&state=RandStringSomea" +
                            "&redirect_uri=profilefetcher%3A%2F%2Fhelloworld"
                ), CUSTOM_TAB_REQUEST_CODE
            ) // TODO: Context or Activity
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CUSTOM_TAB_REQUEST_CODE) {
            signIn.isClickable = true
        }
    }

    private fun CustomTabsIntent.launchUrlWithActivityResult(uri: Uri, requestCode: Int) {
        startActivityForResult(this.intent.apply {
            data = uri
        }, requestCode)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        newIntentListenerHolder.removeListener(this)
    }

    override fun onNewIntent(intent: Intent?) {
        if (intent?.dataString != null) {
            signIn?.displayedChild = 1

            val uri = Uri.parse(intent.dataString)
            if (uri != null) {
                val code = uri.getQueryParameter("code")!!
                val state = uri.getQueryParameter("state")!!
                loadAuthToken(code, state)
            }
        }
    }

    private fun loadAuthToken(code: String, state: String) {
        nowLoadingAuthToken = true

        ViewModelProviders.of(activity!!)
            .get(AuthViewModel::class.java)
            .getAuthToken(code, state)
            .observeWithErrorHandling(this, ::onSuccess, ::onError)
    }

    private fun tryRestoreAuthToken() {
        ViewModelProviders.of(activity!!)
            .get(AuthViewModel::class.java)
            .tryRestoreAuthToken()
            ?.observeWithErrorHandling(this, ::onSuccess, ::onError)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (nowLoadingAuthToken) ViewModelProviders.of(activity!!)
            .get(AuthViewModel::class.java)
            .tryRestoreAuthToken()
            ?.removeObservers(this)
    }

    private fun onSuccess(result: String) {
        Log.d("TOKEN success", result)

        nowLoadingAuthToken = false

        activity!!.getPreferences(Activity.MODE_PRIVATE)
            .edit()
            .putString(MainActivity.AUTH_TOKEN_KEY, result)
            .apply()

        detectAuthTokenListener.onDetectAuthToken(result)
    }

    private fun onError(throwable: Throwable) {
        Log.d("TAG", "notok") // FIXME: !!!!
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        nowLoadingAuthToken = savedInstanceState?.getBoolean(BUTTON_STATE_KEY, false) ?: false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BUTTON_STATE_KEY, nowLoadingAuthToken)
    }


}
