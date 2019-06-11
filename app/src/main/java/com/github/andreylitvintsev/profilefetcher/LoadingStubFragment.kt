package com.github.andreylitvintsev.profilefetcher

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.github.andreylitvintsev.profilefetcher.viewmodel.DataRepositoryViewModel
import com.github.andreylitvintsev.profilefetcher.viewmodel.DataRepositoryViewModelFactory
import com.github.andreylitvintsev.profilefetcher.viewmodel.observeEventWithErrorHandling
import kotlinx.android.synthetic.main.fragment_loading_stub.*
import java.net.ConnectException
import java.net.UnknownHostException


class LoadingStubFragment : Fragment() {

    companion object {
        fun instantiate(authToken: String) = LoadingStubFragment().apply {
            this.authToken = authToken
        }
    }

    private lateinit var authToken: String

    private lateinit var connectivityService: ConnectivityManager

    private lateinit var dataRepositoryViewModel: DataRepositoryViewModel

    private var profileDataIsReady = false
    private var projectRepositoriesDataIsReady = false

    private var profileDataIsAuthFailure = false
    private var projectRepositoriesDataIsAuthFailure = false

    private lateinit var networkConnectivityCallbacks: ConnectivityManager.NetworkCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("TAG", "onAttach")

        dataRepositoryViewModel = ViewModelProviders.of(
            activity!!, DataRepositoryViewModelFactory(authToken, activity!!.application)
        ).get(DataRepositoryViewModel::class.java)

        Log.d("TOKEN", "new token $authToken")
        if (dataRepositoryViewModel.authToken != authToken) {
            dataRepositoryViewModel.authToken = authToken
        }

        connectivityService = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        tryLoadData()

        networkConnectivityCallbacks = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network?) {
                activity?.runOnUiThread {
                    rootViewAnimator?.displayedChild = 0
                    dataRepositoryViewModel.reload()
                }
            }
        }

        connectivityService.registerNetworkCallback(
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
            networkConnectivityCallbacks
        )
    }

    override fun onDetach() {
        super.onDetach()
        connectivityService.unregisterNetworkCallback(networkConnectivityCallbacks)
    }

    private fun tryLoadData() {
        dataRepositoryViewModel.apply {
            getProfile().observeEventWithErrorHandling(
                this@LoadingStubFragment,
                onSuccess = { data, isNewContent ->
                    println("tryLoadData 1")
                    profileDataIsReady = true
                    if (projectRepositoriesDataIsReady) openMainFragment()
                },
                onError = { throwable, isNewContent ->
                    println("tryLoadData 1")

                    if (checkThrowableForNetworkTrouble(throwable)) {
                        rootViewAnimator.displayedChild = 1
                    } else if (isNewContent) {
                        profileDataIsAuthFailure = true
                        checkForInvalidAuthTokenAndExit()
                        Toast.makeText(activity!!, "Unknown error", Toast.LENGTH_SHORT)
                            .show() // TODO: появляюсь в случае если второй обогнал
                    }
                }
            )
            getRepositories().observeEventWithErrorHandling(
                this@LoadingStubFragment,
                onSuccess = { data, isNewContent ->
                    println("tryLoadData 2")
                    projectRepositoriesDataIsReady = true
                    if (profileDataIsReady) openMainFragment()
                },
                onError = { throwable, isNewContent ->
                    println("tryLoadData 2")
                    if (checkThrowableForNetworkTrouble(throwable)) {
                        rootViewAnimator.displayedChild = 1
                    } else if (isNewContent) {
                        projectRepositoriesDataIsAuthFailure = true
                        checkForInvalidAuthTokenAndExit()
                        Toast.makeText(activity!!, "Unknown error", Toast.LENGTH_SHORT).show() // TODO: back to auth
                    }
                }
            )
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        println("onViewStateRestored")
        profileDataIsAuthFailure = savedInstanceState?.getBoolean("profileDataIsAuthFailure") ?: false
        projectRepositoriesDataIsAuthFailure = savedInstanceState?.getBoolean("projectRepositoriesDataIsAuthFailure") ?: false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("profileDataIsAuthFailure", profileDataIsAuthFailure)
        outState.putBoolean("projectRepositoriesDataIsAuthFailure", projectRepositoriesDataIsAuthFailure)
    }

    private fun checkForInvalidAuthTokenAndExit() {
        if (profileDataIsAuthFailure && projectRepositoriesDataIsAuthFailure) {
            profileDataIsAuthFailure = false
            projectRepositoriesDataIsAuthFailure = false
            activity!!.getPreferences(Activity.MODE_PRIVATE).edit()
                .putString(MainActivity.AUTH_TOKEN_KEY, "").apply()
            activity!!.supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, AuthFragment()).commit()
        }
    }

    private fun openMainFragment() {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(android.R.id.content, MainFragment())
            ?.commit()
    }

    private fun checkThrowableForNetworkTrouble(throwable: Throwable): Boolean {
        return (throwable is ConnectException || throwable is UnknownHostException)
                && rootViewAnimator.displayedChild == 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loading_stub, container, false)
    }

}
