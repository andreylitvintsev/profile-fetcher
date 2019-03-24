package com.github.andreylitvintsev.profilefetcher.viewmodel

import androidx.lifecycle.LiveData
import com.github.andreylitvintsev.profilefetcher.repository.DataRepository
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository

// TODO: можно привести к использванию обобщений
class ProjectRepositoriesLiveData(
    private val dataRepository: DataRepository
) : LiveData<DataWrapperForErrorHanding<List<ProjectRepository>>>() {

    private var alreadyLoaded = false

    private lateinit var dismisser: DataRepository.Dismisser

    override fun onInactive() {
        if (!hasObservers()) dismisser.dismiss()
    }

    override fun onActive() {
        if (!alreadyLoaded) {
            dismisser = dataRepository.getRepositories(
                resultCallback = { result ->
                    alreadyLoaded = result.throwable == null
                    value = result
                }
            )
        }
    }

}
