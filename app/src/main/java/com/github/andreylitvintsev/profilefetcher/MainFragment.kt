package com.github.andreylitvintsev.profilefetcher

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository
import com.github.andreylitvintsev.profilefetcher.viewmodel.DataRepositoryViewModel
import com.github.andreylitvintsev.profilefetcher.viewmodel.observeEventWithErrorHandling
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
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataAdapter = DataAdapter(customTabsIntent, ::logout)

        // TODO: корректно настроить
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = dataAdapter

        dataRepositoryViewModel.getProfile().observeEventWithErrorHandling(this@MainFragment,
            onSuccess = { profile, _ -> dataAdapter.setProfile(profile) }
        )
        dataRepositoryViewModel.getRepositories().observeEventWithErrorHandling(this@MainFragment,
            onSuccess = { projectRepositories, _ -> dataAdapter.setProjectRepositories(projectRepositories) }
        )
    }

    private fun logout() {
        activity!!.getPreferences(Activity.MODE_PRIVATE).edit()
            .putString(MainActivity.AUTH_TOKEN_KEY, "").apply()
        activity!!.supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, AuthFragment()).commit()
    }

}

class DataAdapter(
    private val customTabsIntent: CustomTabsIntent,
    private val logoutCallback: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val PROFILE_VIEW_TYPE = 0
    private val PROJECT_REPOSITORY_VIEW_TYPE = 1

    private var profile: Profile? = null
    private var projectRepositories: List<ProjectRepository>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PROFILE_VIEW_TYPE -> ProfileViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_profile_info, parent, false),
                customTabsIntent,
                logoutCallback
            )
            else -> ProjectRepositoryViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_project_repository, parent, false),
                customTabsIntent
            )
        }
    }

    fun setProfile(profile: Profile) {
        this.profile = profile
        notifyDataSetChanged()
    }

    fun setProjectRepositories(projectRepositories: List<ProjectRepository>) {
        this.projectRepositories = projectRepositories
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = (projectRepositories?.size ?: 0) + 1 // "project repositories" + "profile"

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            (holder as ProfileViewHolder).onBindViewHolder(profile)
        } else {
            (holder as ProjectRepositoryViewHolder).onBindViewHolder(projectRepositories?.getOrNull(position - 1))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> PROFILE_VIEW_TYPE
            else -> PROJECT_REPOSITORY_VIEW_TYPE
        }
    }
}


private class ProfileViewHolder(
    itemView: View,
    private val customTabsIntent: CustomTabsIntent,
    logoutCallback: () -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val loginView = itemView.findViewById<TextView>(R.id.loginView)
    private val nameView = itemView.findViewById<TextView>(R.id.nameView)
    private val locationView = itemView.findViewById<TextView>(R.id.locationView)
    private val avatarView = itemView.findViewById<ImageView>(R.id.avatarView)
    private val githubButton = itemView.findViewById<View>(R.id.githubButton)

    init {
        initToolbar(logoutCallback)
    }

    fun onBindViewHolder(profile: Profile?) {
        if (profile == null) return

        loginView.text = profile.login
        nameView.text = profile.name
        locationView.text = profile.location
        avatarView.loadImageFrom(profile.avatarUrl)
        githubButton.setOnClickListener {
            customTabsIntent.launchUrl(it.context, Uri.parse(profile.url))
        }
    }

    private fun initToolbar(logoutCallback: () -> Unit) {
        itemView.findViewById<Toolbar>(R.id.toolbar).apply {
            inflateMenu(R.menu.main_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.logout -> {
                        logoutCallback.invoke()
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun ImageView.loadImageFrom(imageUrl: String) {
        GlideApp.with(context)
            .load(imageUrl)
            .circleCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }

}

private class ProjectRepositoryViewHolder(
    itemView: View,
    private val customTabsIntent: CustomTabsIntent
) : RecyclerView.ViewHolder(itemView) {

    private val isPrivateView = itemView.findViewById<ImageView>(R.id.isPrivateView)
    private val projectNameView = itemView.findViewById<TextView>(R.id.projectNameView)

    private val publicDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.ic_lock_open)
    private val privateDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.ic_lock)

    fun onBindViewHolder(projectRepository: ProjectRepository?) {
        if (projectRepository == null) return

        with(projectRepository) {
            itemView.setOnClickListener {
                customTabsIntent.launchUrl(it.context, Uri.parse(url))
            }
            isPrivateView.setImageDrawable(if (isPrivate) privateDrawable else publicDrawable)
            projectNameView.text = this.name
        }
    }

}
