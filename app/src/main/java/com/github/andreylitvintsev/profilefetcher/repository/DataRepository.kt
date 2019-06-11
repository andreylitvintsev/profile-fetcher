package com.github.andreylitvintsev.profilefetcher.repository

import androidx.lifecycle.LiveData
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository
import com.github.andreylitvintsev.profilefetcher.viewmodel.Event


interface DataRepository {
    fun getProfile(): LiveData<Event<DataWrapperForErrorHanding<Profile>>>
    fun getProjectRepositories(): LiveData<Event<DataWrapperForErrorHanding<List<ProjectRepository>>>>
    fun reload(refreshLoadedData: Boolean = false)
    fun reset()
}
