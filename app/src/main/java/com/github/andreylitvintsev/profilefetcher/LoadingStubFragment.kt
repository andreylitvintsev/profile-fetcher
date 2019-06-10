package com.github.andreylitvintsev.profilefetcher

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.github.andreylitvintsev.profilefetcher.viewmodel.DataRepositoryViewModel
import com.github.andreylitvintsev.profilefetcher.viewmodel.DataRepositoryViewModelFactory
import com.github.andreylitvintsev.profilefetcher.viewmodel.observeDataWrapper
import kotlinx.android.synthetic.main.fragment_loading_stub.*
import java.net.ConnectException
import java.net.UnknownHostException


class LoadingStubFragment : Fragment() {

    private lateinit var connectivityService: ConnectivityManager

    private lateinit var dataRepositoryViewModel: DataRepositoryViewModel

    private var profileDataIsReady = false
    private var projectRepositoriesDataIsReady = false

    private lateinit var networkConnectivityCallbacks: ConnectivityManager.NetworkCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)

        dataRepositoryViewModel = ViewModelProviders.of(
            activity!!, DataRepositoryViewModelFactory(BuildConfig.GITHUB_PROFILE_TOKEN, activity!!.application)
        ).get(DataRepositoryViewModel::class.java)

        connectivityService = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        tryLoadData()

        networkConnectivityCallbacks = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network?) {
                activity?.runOnUiThread {
                    rootViewAnimator.displayedChild = 0
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
            getProfile().observe(
                this@LoadingStubFragment, observeDataWrapper(
                    onSuccess = {
                        profileDataIsReady = true
                        if (projectRepositoriesDataIsReady) openMainFragment()
                    },
                    onError = {
                        if (checkThrowableForNetworkTrouble(it)) {
                            rootViewAnimator.displayedChild = 1
                        }
                    }
                ))
            getRepositories().observe(this@LoadingStubFragment, observeDataWrapper(
                onSuccess = {
                    projectRepositoriesDataIsReady = true
                    if (profileDataIsReady) openMainFragment()
                },
                onError = {
                    if (checkThrowableForNetworkTrouble(it)) {
                        rootViewAnimator.displayedChild = 1
                    }
                }
            ))
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
