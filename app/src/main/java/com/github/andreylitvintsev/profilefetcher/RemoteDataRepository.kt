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
        profileLiveData = CachedLiveData(dataDownloader) {
            it.getProfile { result -> handleResult(result) }
        }

        projectRepositoryLiveData = CachedLiveData(dataDownloader) {
            it.getProjectRepositories { result -> handleResult(result) }
        }
    }

    override fun getProfile(): LiveData<DataWrapperForErrorHanding<Profile>> {
        return profileLiveData
    }

    override fun getProjectRepositories(): LiveData<DataWrapperForErrorHanding<List<ProjectRepository>>> {
        return projectRepositoryLiveData
    }

}

private class CachedLiveData<T>( // TODO: подумай хорошенько
    private val dataDownloader: DataDownloader,
    private val body: CachedLiveData<T>.(dataDownloader: DataDownloader) -> DataDownloader.Dismisser
) : LiveData<DataWrapperForErrorHanding<T>>() {

    private var alreadyLoaded = false

    private var dismisser: DataDownloader.Dismisser? = null

    override fun onActive() {
        if (!alreadyLoaded && dismisser == null) {
            dismisser = body.invoke(this, dataDownloader)
        }
    }

    fun handleResult(result: DataWrapperForErrorHanding<T>) {
        alreadyLoaded = result.throwable == null
        value = result
    }

}
