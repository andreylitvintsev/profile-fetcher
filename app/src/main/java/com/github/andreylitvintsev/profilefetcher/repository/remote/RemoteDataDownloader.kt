package com.github.andreylitvintsev.profilefetcher.repository.remote

import android.util.Log
import com.github.andreylitvintsev.profilefetcher.MoshiProvider
import com.github.andreylitvintsev.profilefetcher.OkHttpClientProvider
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class RemoteDataDownloader(
    var authToken: String,
    moshiProvider: MoshiProvider,
    okHttpClientProvider: OkHttpClientProvider
) : DataDownloader {

    private val gitHubApi: GitHubApi

    private var profileCall: Call<Profile>? = null
    private var repositoriesCall: Call<List<ProjectRepository>>? = null

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshiProvider.provideMoshi()))
            .client(okHttpClientProvider.provideOkHttp())
            .build()

        gitHubApi = retrofit.create(GitHubApi::class.java)
    }

    override fun getProfile(
        resultCallback: (dataWrapper: DataWrapperForErrorHanding<Profile>) -> Unit
    ): DataDownloader.Dismisser {
        profileCall?.cancel()
        Log.d("TOKEN", "used $authToken")
        profileCall = gitHubApi.getUserInfo("token $authToken")
        profileCall?.enqueue(object : Callback<Profile> {
            override fun onFailure(call: Call<Profile>, throwable: Throwable) {
                return resultCallback(DataWrapperForErrorHanding(throwable))
            }

            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                resultCallback(handleResponse(response))
            }
        })

        return configureDismisser(profileCall)
    }

    override fun getProjectRepositories(
        resultCallback: (dataWrapper: DataWrapperForErrorHanding<List<ProjectRepository>>) -> Unit
    ): DataDownloader.Dismisser {
        repositoriesCall?.cancel()

        repositoriesCall = gitHubApi.getRepositories("token $authToken")
        repositoriesCall?.enqueue(object : Callback<List<ProjectRepository>> {
            override fun onFailure(call: Call<List<ProjectRepository>>, throwable: Throwable) =
                resultCallback(DataWrapperForErrorHanding(throwable))

            override fun onResponse(call: Call<List<ProjectRepository>>, response: Response<List<ProjectRepository>>) {
                resultCallback(handleResponse(response))
            }
        })

        return configureDismisser(repositoriesCall)
    }

    private fun <T> handleResponse(response: Response<T>): DataWrapperForErrorHanding<T> {
        return if (response.isSuccessful) DataWrapperForErrorHanding(response.body()!!)
        else DataWrapperForErrorHanding(
            IllegalStateException(
                "Response with code ${response.code()}",
                IllegalAccessException(response.code().toString())
            )
        )
    }

    private fun <T> configureDismisser(call: Call<T>?): DataDownloader.Dismisser {
        return object : DataDownloader.Dismisser {
            override fun dismiss() {
                call?.cancel()
            }
        }
    }

}
