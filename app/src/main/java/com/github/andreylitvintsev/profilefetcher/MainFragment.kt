package com.github.andreylitvintsev.profilefetcher

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.andreylitvintsev.profilefetcher.viewmodel.DataRepositoryViewModel
import com.github.andreylitvintsev.profilefetcher.viewmodel.observeDataWrapper
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment() {

    private lateinit var dataRepositoryViewModel: DataRepositoryViewModel

    private lateinit var customTabsIntent: CustomTabsIntent

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataRepositoryViewModel = ViewModelProviders.of(context as FragmentActivity)
            .get(DataRepositoryViewModel::class.java)

        customTabsIntent = CustomTabsIntent.Builder().build()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false).apply {
            dataRepositoryViewModel.getProfile().observe(this@MainFragment, observeDataWrapper(
                onSuccess = { profile ->
                    loginView.text = profile.login
                    nameView.text = profile.name
                    locationView.text = profile.location
                    avatarView.loadImageFrom(profile.avatarUrl)
                    githubButton.setOnClickListener {
                        customTabsIntent.launchUrl(context, Uri.parse(profile.url))
                    }
                })
            )
        }
    }

    private fun ImageView.loadImageFrom(imageUrl: String) {
        GlideApp.with(this@MainFragment)
            .load(imageUrl)
            .circleCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }

}
