package com.github.andreylitvintsev.profilefetcher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.github.andreylitvintsev.profilefetcher.DatabaseProvider
import com.github.andreylitvintsev.profilefetcher.RemoteDataRepository
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import com.github.andreylitvintsev.profilefetcher.repository.LocalDataRepository
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.remote.RemoteDataDownloader


class DataRepositoryViewModel(application: Application) : AndroidViewModel(application) {

    private val dataDownloader = RemoteDataDownloader()
    private val databaseProvider = application as DatabaseProvider

    private val remoteDataRepository = RemoteDataRepository(dataDownloader)
    private val localDataRepository = LocalDataRepository(databaseProvider)

    private val profileMediatorLiveData = MediatorLiveData<DataWrapperForErrorHanding<Profile>>().apply {
        addSource(localDataRepository.getProfile()) { localResult ->
            if (localResult.fetchedData == null) {
                addSource(remoteDataRepository.getProfile()) { remoteResult ->
                    if (remoteResult.fetchedData != null) {
                        localDataRepository.updateProfile(remoteResult.fetchedData)
                    }
                    value = remoteResult
                }
            } else {
                value = localResult
            }
        }
    }

    fun getProfile(): LiveData<DataWrapperForErrorHanding<Profile>> {
        return profileMediatorLiveData
    }

}
