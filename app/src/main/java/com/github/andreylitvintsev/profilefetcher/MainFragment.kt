package com.github.andreylitvintsev.profilefetcher

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.andreylitvintsev.profilefetcher.viewmodel.DataRepositoryViewModel
import com.github.andreylitvintsev.profilefetcher.viewmodel.observeDataWrapper
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*


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
//            dataRepositoryViewModel.getProfile().observe(
//                this@MainFragment, observeDataWrapper(
//                    onSuccess = { profile ->
//                        loginView.text = profile.login
//                        nameView.text = profile.name
//                        locationView.text = profile.location
//                        avatarView.loadImageFrom(profile.avatarUrl)
//                        githubButton.setOnClickListener {
//                            customTabsIntent.launchUrl(context, Uri.parse(profile.url))
//                        }
//                    })
//            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = DataAdapter()
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context).also { // TODO: корректно настроить
            it.initialPrefetchItemCount = 10
            it.isItemPrefetchEnabled = true
        }
    }

    private fun ImageView.loadImageFrom(imageUrl: String) {
        GlideApp.with(this@MainFragment)
            .load(imageUrl)
            .circleCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }

    private class DataAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        companion object {
            const val PROFILE_VIEW_TYPE = 0
            const val PROJECT_REPOSITORY_VIEW_TYPE = 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when(viewType) {
                PROFILE_VIEW_TYPE -> ProfileViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_info, parent, false))
                else -> ProjectRepositoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_project_repository, parent, false)) // PROJECT_REPOSITORY_VIEW_TYPE
            }
        }

        override fun getItemCount(): Int = 30

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        }

        override fun getItemViewType(position: Int): Int {
            return when (position) {
                0 -> PROFILE_VIEW_TYPE
                else -> PROJECT_REPOSITORY_VIEW_TYPE
            }
        }
    }

    private class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    private class ProjectRepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
