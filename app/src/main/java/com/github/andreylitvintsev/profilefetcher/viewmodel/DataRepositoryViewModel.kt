package com.github.andreylitvintsev.profilefetcher.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.github.andreylitvintsev.profilefetcher.DatabaseProvider
import com.github.andreylitvintsev.profilefetcher.MoshiProvider
import com.github.andreylitvintsev.profilefetcher.OkHttpClientProvider
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import com.github.andreylitvintsev.profilefetcher.repository.local.LocalDataRepository
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository
import com.github.andreylitvintsev.profilefetcher.repository.remote.RemoteDataDownloader
import com.github.andreylitvintsev.profilefetcher.repository.remote.RemoteDataRepository


class DataRepositoryViewModel(
    authToken: String,
    application: Application
) : AndroidViewModel(application) {

    private val databaseProvider = application as DatabaseProvider
    private val moshiProvider = application as MoshiProvider
    private val okHttpClientProvider = application as OkHttpClientProvider

    private val dataDownloader = RemoteDataDownloader(authToken, moshiProvider, okHttpClientProvider)

    var authToken: String
        get() = dataDownloader.authToken
        set(value) {
            dataDownloader.authToken = value
            remoteDataRepository.reset()
        }

    private val remoteDataRepository = RemoteDataRepository(dataDownloader)
    private val localDataRepository = LocalDataRepository(databaseProvider)

    private val profileMediatorLiveData = lazy(LazyThreadSafetyMode.NONE) {
        Log.d("TAG", "New live data profile")
        createMediatorLiveData(
            localDataRepository.getProfile(),
            remoteDataRepository.getProfile(),
            localDataRepository::updateProfile
        )
    }

    private val projectRepositoriesMediatorLiveData = lazy(LazyThreadSafetyMode.NONE) {
        Log.d("TAG", "New live data mediator")
        createMediatorLiveData(
            localDataRepository.getProjectRepositories(),
            remoteDataRepository.getProjectRepositories(),
            localDataRepository::upsertProjectRepositories
        )
    }

    fun getProfile(): LiveData<Event<DataWrapperForErrorHanding<Profile>>> {
        return profileMediatorLiveData.value
    }

    fun getRepositories(): LiveData<Event<DataWrapperForErrorHanding<List<ProjectRepository>>>> {
        return projectRepositoriesMediatorLiveData.value
    }

    fun reload() = remoteDataRepository.reload()

    private inline fun <T> createMediatorLiveData(
        localSource: LiveData<Event<DataWrapperForErrorHanding<T>>>,
        remoteSource: LiveData<Event<DataWrapperForErrorHanding<T>>>,
        crossinline persistData: (data: T) -> Unit
    ): MediatorLiveData<Event<DataWrapperForErrorHanding<T>>> {

        val mediatorLiveData = MediatorLiveData<Event<DataWrapperForErrorHanding<T>>>()

        mediatorLiveData.addSource(remoteSource) { remoteResult ->
            val content = remoteResult.slightlyPeekContent()
            when {
                content.throwable == null -> {
                    persistData(remoteResult.slightlyPeekContent().fetchedData!!)
                    mediatorLiveData.value = remoteResult
                }

                content.throwable.cause is IllegalAccessException -> {
                    mediatorLiveData.value = remoteResult
                }

                else -> {
                    mediatorLiveData.addSource(localSource) { localResult ->
                        if (content.fetchedData != null) {
                            mediatorLiveData.value = localResult
                        } else {
                            mediatorLiveData.value = remoteResult
                        }
                        mediatorLiveData.removeSource(localSource)
                    }
                }
            }
        }

        return mediatorLiveData
    }

}
