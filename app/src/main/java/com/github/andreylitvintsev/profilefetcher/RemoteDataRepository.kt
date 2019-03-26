package com.github.andreylitvintsev.profilefetcher

import androidx.lifecycle.LiveData
import com.github.andreylitvintsev.profilefetcher.repository.DataRepository
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository
import com.github.andreylitvintsev.profilefetcher.repository.remote.DataDownloader


class RemoteDataRepository(dataDownloader: DataDownloader) : DataRepository {

    private val profileLiveData: LiveData<DataWrapperForErrorHanding<Profile>>
    private val projectRepositoryLiveData: LiveData<DataWrapperForErrorHanding<List<ProjectRepository>>>

    init {
        profileLiveData = AutoDismisserLiveData(dataDownloader) { dataDownloader ->
            dataDownloader.getProfile { result -> handleResult(result) }
        }

        projectRepositoryLiveData = AutoDismisserLiveData(dataDownloader) { dataDownloader ->
            dataDownloader.getProjectRepositories { result -> handleResult(result) }
        }
    }

    override fun getProfile(): LiveData<DataWrapperForErrorHanding<Profile>> {
        return profileLiveData
    }

    override fun getProjectRepositories(): LiveData<DataWrapperForErrorHanding<List<ProjectRepository>>> {
        return projectRepositoryLiveData
    }

}

private class AutoDismisserLiveData<T>(
    private val dataDownloader: DataDownloader,
    private val body: AutoDismisserLiveData<T>.(dataDownloader: DataDownloader) -> DataDownloader.Dismisser
) : LiveData<DataWrapperForErrorHanding<T>>() {

    private var alreadyLoaded = false

    private lateinit var dismisser: DataDownloader.Dismisser

    override fun onInactive() {
        if (!hasObservers()) dismisser.dismiss()
    }

    override fun onActive() {
        if (!alreadyLoaded) {
            dismisser = body.invoke(this, dataDownloader)
        }
    }

    fun handleResult(result: DataWrapperForErrorHanding<T>) {
        alreadyLoaded = result.throwable == null
        value = result
    }

}
