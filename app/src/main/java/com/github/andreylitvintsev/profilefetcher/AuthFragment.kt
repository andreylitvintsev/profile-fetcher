package com.github.andreylitvintsev.profilefetcher

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_auth.*


class AuthFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val customTabsIntent = CustomTabsIntent.Builder().build()
        signIn.setOnClickListener {
            customTabsIntent.launchUrl(activity, Uri.parse("https://github.com/login/oauth/authorize?client_id=d43b1ed587684df51150&scope=user&state=RandStringSomea&redirect_uri=profilefetcher%3A%2F%2Fhelloworld")) // TODO: Context or Activity
        }
    }

}
