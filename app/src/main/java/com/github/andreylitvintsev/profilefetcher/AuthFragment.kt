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
import com.github.andreylitvintsev.profilefetcher.viewmodel.observeDataWrapper
import kotlinx.android.synthetic.main.fragment_auth.*


class AuthFragment : Fragment(), OnNewIntentListener {

    private lateinit var newIntentListenerHolder: NewIntentListenerHolder
    private lateinit var detectAuthTokenListener: DetectAuthTokenListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false).also {
            newIntentListenerHolder = (activity as? NewIntentListenerHolder)
                ?: error("${activity.toString()} must must implement NewIntentListenerHolder!")

            detectAuthTokenListener = (activity as? DetectAuthTokenListener)
                ?: error("${activity.toString()} must must implement DetectAuthTokenListener!")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newIntentListenerHolder.addOnNewIntentListener(this)

        val customTabsIntent = CustomTabsIntent.Builder().build()
        signIn.setOnClickListener {
            customTabsIntent.launchUrl(
                activity,
                Uri.parse("https://github.com/login/oauth/authorize?client_id=d43b1ed587684df51150&scope=user&state=RandStringSomea&redirect_uri=profilefetcher%3A%2F%2Fhelloworld")
            ) // TODO: Context or Activity
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        newIntentListenerHolder.removeListener(this)
    }

    override fun onNewIntent(intent: Intent?) {
        if (intent?.dataString != null) {
            val uri = Uri.parse(intent.dataString) // FIXME
            if (uri != null) {
                ViewModelProviders.of(activity!!)
                    .get(AuthViewModel::class.java)
                    .getAuthToken(
                        uri.getQueryParameter("code")!!,
                        uri.getQueryParameter("state")!!
                    )
                    .observe(this, observeDataWrapper(::onSuccess, ::onError))
            }
        }
    }

    private fun onSuccess(result: String) {
        activity!!.getPreferences(Activity.MODE_PRIVATE)
            .edit()
            .putString(MainActivity.AUTH_TOKEN_KEY, result)
            .apply()

        detectAuthTokenListener.onDetectAuthToken(result)
    }

    private fun onError(throwable: Throwable) {
        Log.d("TAG", "notok")
    }

}
