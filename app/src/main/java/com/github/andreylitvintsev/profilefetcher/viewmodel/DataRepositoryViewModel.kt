package com.github.andreylitvintsev.profilefetcher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.github.andreylitvintsev.profilefetcher.DatabaseProvider
import com.github.andreylitvintsev.profilefetcher.RemoteDataRepository
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import com.github.andreylitvintsev.profilefetcher.repository.LocalDataRepository
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository
import com.github.andreylitvintsev.profilefetcher.repository.remote.RemoteDataDownloader


class DataRepositoryViewModel(application: Application) : AndroidViewModel(application) {

    private val dataDownloader = RemoteDataDownloader()
    private val databaseProvider = application as DatabaseProvider

    private val remoteDataRepository = RemoteDataRepository(dataDownloader)
    private val localDataRepository = LocalDataRepository(databaseProvider)

    private val profileMediatorLiveData = createMediatorLiveData(
        localDataRepository.getProfile(),
        remoteDataRepository.getProfile(),
        localDataRepository::updateProfile
    )

    private val projectRepositoriesMediatorLiveData = createMediatorLiveData(
        localDataRepository.getProjectRepositories(),
        remoteDataRepository.getProjectRepositories(),
        localDataRepository::upsertProjectRepositories
    )

    fun getProfile(): LiveData<DataWrapperForErrorHanding<Profile>> {
        return profileMediatorLiveData
    }

    fun getRepositories(): LiveData<DataWrapperForErrorHanding<List<ProjectRepository>>> {
        return projectRepositoriesMediatorLiveData
    }

    fun reload() = remoteDataRepository.reload()

    private inline fun <T> createMediatorLiveData(
        localSource: LiveData<DataWrapperForErrorHanding<T>>,
        remoteSource: LiveData<DataWrapperForErrorHanding<T>>,
        crossinline persistData: (data: T) -> Unit
    ): MediatorLiveData<DataWrapperForErrorHanding<T>> {

        val mediatorLiveData = MediatorLiveData<DataWrapperForErrorHanding<T>>()

        mediatorLiveData.addSource(remoteSource) { remoteResult ->

            if (remoteResult.throwable == null) {
                persistData(remoteResult.fetchedData!!)
                mediatorLiveData.value = remoteResult

            } else {
                mediatorLiveData.addSource(localSource) { localResult ->
                    mediatorLiveData.value = localResult
                    mediatorLiveData.removeSource(localSource)
                }
            }
        }

        return mediatorLiveData
    }

//    private inline fun <T> createMediatorLiveData(
//        localSource: LiveData<DataWrapperForErrorHanding<T>>,
//        remoteSource: LiveData<DataWrapperForErrorHanding<T>>,
//        crossinline persistData: (data: T) -> Unit
//    ): MediatorLiveData<DataWrapperForErrorHanding<T>> {
//
//        val mediatorLiveData = MediatorLiveData<DataWrapperForErrorHanding<T>>()
//
//        mediatorLiveData.addSource(localSource) { localResult ->
//            if (localResult.fetchedData == null || (localResult.fetchedData as? Collection<*>)?.isEmpty() == true) {
//
//                mediatorLiveData.addSource(remoteSource) { remoteResult ->
//
//                    if (remoteResult.fetchedData != null) {
//                        persistData(remoteResult.fetchedData)
//                    }
//                    mediatorLiveData.value = remoteResult
//                }
//
//            } else {
//                mediatorLiveData.value = localResult
//            }
//        }
//
//        return mediatorLiveData
//    }


}
