package com.github.andreylitvintsev.profilefetcher.repository.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.andreylitvintsev.profilefetcher.repository.DataRepository
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository
import com.github.andreylitvintsev.profilefetcher.repository.remote.DataDownloader


class RemoteDataRepository(private val dataDownloader: DataDownloader) : DataRepository {

    private val profileLiveData = MutableLiveData<DataWrapperForErrorHanding<Profile>>()
    private val projectRepositoryLiveData = MutableLiveData<DataWrapperForErrorHanding<List<ProjectRepository>>>()

    private var downloadProfileInProgress = false
    private var downloadProjectRepositoriesInProgress = false

    override fun getProfile(): LiveData<DataWrapperForErrorHanding<Profile>> {
        downloadProfile()
        return profileLiveData
    }

    private fun downloadProfile() {
        if (!downloadProfileInProgress) {
            downloadProfileInProgress = true

            dataDownloader.getProfile {
                profileLiveData.value = it
                downloadProfileInProgress = false
            }

        }
    }

    override fun getProjectRepositories(): LiveData<DataWrapperForErrorHanding<List<ProjectRepository>>> {
        downloadProjectRepositories()
        return projectRepositoryLiveData
    }

    private fun downloadProjectRepositories() {
        if (!downloadProjectRepositoriesInProgress) {
            downloadProjectRepositoriesInProgress = true

            dataDownloader.getProjectRepositories {
                projectRepositoryLiveData.value = it
                downloadProjectRepositoriesInProgress = false
            }
        }
    }

    override fun reload() {
        downloadProfile()
        downloadProjectRepositories()
    }

}
