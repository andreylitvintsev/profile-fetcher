package com.github.andreylitvintsev.profilefetcher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
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
    private val projectRepositoriesLiveData = LocalDataRepository(databaseProvider)

    fun getProfile(): LiveData<DataWrapperForErrorHanding<Profile>> {
        return remoteDataRepository.getProfile()
    }

    fun updateProfile(profile: Profile) {
        projectRepositoriesLiveData.updateProfile(profile)
    }

}
