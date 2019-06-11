package com.github.andreylitvintsev.profilefetcher.repository.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.andreylitvintsev.profilefetcher.repository.DataRepository
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository


class RemoteDataRepository(private val dataDownloader: DataDownloader) : DataRepository {

    private val profileLiveData = MutableLiveData<DataWrapperForErrorHanding<Profile>>()
    private val projectRepositoryLiveData = MutableLiveData<DataWrapperForErrorHanding<List<ProjectRepository>>>()

    private var downloadProfileInProgress = false
    private var downloadProjectRepositoriesInProgress = false

    private var alreadyProfileDownloaded = false
    private var alreadyProjectRepositoriesDownloaded = false

    override fun getProfile(): LiveData<DataWrapperForErrorHanding<Profile>> {
        downloadProfile()
        return profileLiveData
    }

    private fun downloadProfile(refreshLoadedData: Boolean = false) {
        if (alreadyProfileDownloaded && !refreshLoadedData) return

        if (!downloadProfileInProgress) {
            downloadProfileInProgress = true

            dataDownloader.getProfile {
                profileLiveData.value = it
                downloadProfileInProgress = false
                alreadyProfileDownloaded = true
            }

        }
    }

    override fun getProjectRepositories(): LiveData<DataWrapperForErrorHanding<List<ProjectRepository>>> {
        downloadProjectRepositories()
        return projectRepositoryLiveData
    }

    private fun downloadProjectRepositories(refreshLoadedData: Boolean = false) {
        if (alreadyProjectRepositoriesDownloaded && !refreshLoadedData) return

        if (!downloadProjectRepositoriesInProgress) {
            downloadProjectRepositoriesInProgress = true

            dataDownloader.getProjectRepositories {
                projectRepositoryLiveData.value = it
                downloadProjectRepositoriesInProgress = false
                alreadyProjectRepositoriesDownloaded = true
            }
        }
    }

    override fun reload(refreshLoadedData: Boolean) {
        downloadProfile(refreshLoadedData)
        downloadProjectRepositories(refreshLoadedData)
    }
}
