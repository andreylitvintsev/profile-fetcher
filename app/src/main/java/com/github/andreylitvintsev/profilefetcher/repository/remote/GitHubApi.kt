package com.github.andreylitvintsev.profilefetcher.repository.remote

import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository
import retrofit2.Call
import retrofit2.http.GET

interface GitHubApi {
    @GET("user")
    fun getUserInfo(): Call<Profile>

    @GET("user/repos")
    fun getRepositories(): Call<List<ProjectRepository>>
}
