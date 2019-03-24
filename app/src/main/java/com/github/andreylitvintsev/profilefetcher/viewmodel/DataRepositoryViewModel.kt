package com.github.andreylitvintsev.profilefetcher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.github.andreylitvintsev.profilefetcher.repository.remote.RemoteDataRepository


class DataRepositoryViewModel(application: Application) : AndroidViewModel(application) {

    private val dataRepository = RemoteDataRepository()

    val profileLiveData = ProfileLiveData(dataRepository)
    val projectRepositoriesLiveData = ProjectRepositoriesLiveData(dataRepository)


}
