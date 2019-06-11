package com.github.andreylitvintsev.profilefetcher.repository.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.andreylitvintsev.profilefetcher.repository.DataRepository
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository
import com.github.andreylitvintsev.profilefetcher.viewmodel.Event


class RemoteDataRepository(private val dataDownloader: DataDownloader) : DataRepository {

    private val profileLiveData = MutableLiveData<Event<DataWrapperForErrorHanding<Profile>>>()
    private val projectRepositoryLiveData = MutableLiveData<Event<DataWrapperForErrorHanding<List<ProjectRepository>>>>()

    private var downloadProfileDismisser: DataDownloader.Dismisser? = null
    private var downloadProjectReposirotiesDismisser: DataDownloader.Dismisser? = null

    private var alreadyProfileDownloaded = false
    private var alreadyProjectRepositoriesDownloaded = false

    override fun getProfile(): LiveData<Event<DataWrapperForErrorHanding<Profile>>> {
        downloadProfile()
        return profileLiveData
    }

    private fun downloadProfile(refreshLoadedData: Boolean = false) {
        if (alreadyProfileDownloaded && !refreshLoadedData) return

        if (downloadProfileDismisser == null) {

            downloadProfileDismisser = dataDownloader.getProfile {
                profileLiveData.value = Event(it)
                downloadProfileDismisser = null
                alreadyProfileDownloaded = true
            }

        }
    }

    override fun getProjectRepositories(): LiveData<Event<DataWrapperForErrorHanding<List<ProjectRepository>>>> {
        downloadProjectRepositories()
        return projectRepositoryLiveData
    }

    private fun downloadProjectRepositories(refreshLoadedData: Boolean = false) {
        if (alreadyProjectRepositoriesDownloaded && !refreshLoadedData) return

        if (downloadProjectReposirotiesDismisser == null) {

            downloadProjectReposirotiesDismisser = dataDownloader.getProjectRepositories {
                projectRepositoryLiveData.value = Event(it)
                downloadProjectReposirotiesDismisser = null
                alreadyProjectRepositoriesDownloaded = true
            }
        }
    }

    override fun reload(refreshLoadedData: Boolean) {
        downloadProfile(refreshLoadedData)
        downloadProjectRepositories(refreshLoadedData)
    }

    override fun reset() {
        downloadProfileDismisser?.dismiss()
        downloadProfileDismisser = null
        alreadyProfileDownloaded = false

        downloadProjectReposirotiesDismisser?.dismiss()
        downloadProjectReposirotiesDismisser = null
        alreadyProjectRepositoriesDownloaded = false
    }

}
