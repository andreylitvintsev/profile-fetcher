package com.github.andreylitvintsev.profilefetcher.viewmodel

import androidx.lifecycle.LiveData
import com.github.andreylitvintsev.profilefetcher.repository.remote.DataDownloader
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository

class ProjectRepositoriesLiveData(
    private val dataDownloader: DataDownloader
) : LiveData<DataWrapperForErrorHanding<List<ProjectRepository>>>() {

    private var alreadyLoaded = false

    private lateinit var dismisser: DataDownloader.Dismisser

    override fun onInactive() {
        if (!hasObservers()) dismisser.dismiss()
    }

    override fun onActive() {
        if (!alreadyLoaded) {
            dismisser = dataDownloader.getProjectRepositories(
                resultCallback = { result ->
                    alreadyLoaded = result.throwable == null
                    value = result
                }
            )
        }
    }

}
