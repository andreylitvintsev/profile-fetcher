package com.github.andreylitvintsev.profilefetcher

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

    override fun getProfile(): LiveData<DataWrapperForErrorHanding<Profile>> {
        downloadProfile()
        return profileLiveData
    }

    private fun downloadProfile() {
        dataDownloader.getProfile {
            profileLiveData.value = it
        }
    }

    override fun getProjectRepositories(): LiveData<DataWrapperForErrorHanding<List<ProjectRepository>>> {
        downloadProjectRepositories()
        return projectRepositoryLiveData
    }

    private fun downloadProjectRepositories() {
        dataDownloader.getProjectRepositories {
            projectRepositoryLiveData.value = it
        }
    }

    override fun reload() {
        downloadProfile()
        downloadProjectRepositories()
    }

}
