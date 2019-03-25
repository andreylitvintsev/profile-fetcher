package com.github.andreylitvintsev.profilefetcher.repository.remote

import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository


interface DataDownloader {

    interface Dismisser {
        fun dismiss()
    }

    fun getProfile(resultCallback: (dataWrapper: DataWrapperForErrorHanding<Profile>) -> Unit): Dismisser

    fun getProjectRepositories(resultCallback: (dataWrapper: DataWrapperForErrorHanding<List<ProjectRepository>>) -> Unit): Dismisser

}
