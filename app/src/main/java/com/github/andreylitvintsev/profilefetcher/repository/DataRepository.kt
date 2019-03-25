package com.github.andreylitvintsev.profilefetcher.repository

import androidx.lifecycle.LiveData
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository


interface DataRepository {
    fun getProfile(): LiveData<DataWrapperForErrorHanding<Profile>>
    fun getProjectRepositories(): LiveData<DataWrapperForErrorHanding<List<ProjectRepository>>>
}
