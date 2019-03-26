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
import com.github.andreylitvintsev.profilefetcher.viewmodel.observeDataWrapper
import kotlinx.android.synthetic.main.fragment_loading_stub.*


class LoadingStubFragment : Fragment() {

    private lateinit var connectivityService: ConnectivityManager

    override fun onAttach(context: Context) {
        super.onAttach(context)

        connectivityService = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityService.registerNetworkCallback(
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network?) {
                    activity?.runOnUiThread {
                        rootViewAnimator?.displayedChild = 0
                        loadProfileData()
                    }
                }

                override fun onLost(network: Network?) {
                    activity?.runOnUiThread {
                        rootViewAnimator?.displayedChild = 1
                    }
                }
            })
    }

    private fun loadProfileData() {
        ViewModelProviders.of(activity!!).get(DataRepositoryViewModel::class.java).apply {
            getProfile().observe(this@LoadingStubFragment, observeDataWrapper(
                onSuccess = {
                    activity?.supportFragmentManager?.beginTransaction()?.replace(android.R.id.content, MainFragment())
                        ?.commit()
                })
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loading_stub, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (connectivityService.activeNetworkInfo?.isConnected == true) {
            rootViewAnimator.displayedChild = 0
        } else {
            rootViewAnimator.displayedChild = 1
        }
    }
}
