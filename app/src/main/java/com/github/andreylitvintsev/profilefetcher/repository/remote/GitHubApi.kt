package com.github.andreylitvintsev.profilefetcher.repository.remote

import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header


interface GitHubApi {

    @GET("user")
    fun getUserInfo(@Header("Authorization") token: String): Call<Profile>

    @GET("user/repos")
    fun getRepositories(@Header("Authorization") token: String): Call<List<ProjectRepository>>

}
