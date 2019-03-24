package com.github.andreylitvintsev.profilefetcher.repository

import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository


interface DataRepository {

    interface Dismisser {
        fun dismiss()
    }

    fun getProfile(resultCallback: (dataWrapper: DataWrapperForErrorHanding<Profile>) -> Unit): Dismisser

    fun getRepositories(resultCallback: (dataWrapper: DataWrapperForErrorHanding<List<ProjectRepository>>) -> Unit): Dismisser

}
