package com.github.andreylitvintsev.profilefetcher.repository.remote

import com.github.andreylitvintsev.profilefetcher.BuildConfig
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class RemoteDataDownloader : DataDownloader {

    private val gitHubApi: GitHubApi

    private var profileCall: Call<Profile>? = null
    private var repositoriesCall: Call<List<ProjectRepository>>? = null

    init {
        val moshi = Moshi.Builder()
            .add(ReducedProfileAdapter())
            .add(ReducedRepositoryAdapter())
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    this.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                }
            }
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "token  ${BuildConfig.GITHUB_PROFILE_TOKEN}")
                    .build()
                chain.proceed(request)
            }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()

        gitHubApi = retrofit.create(GitHubApi::class.java)
    }

    override fun getProfile(
        resultCallback: (dataWrapper: DataWrapperForErrorHanding<Profile>) -> Unit
    ): DataDownloader.Dismisser {
        profileCall?.cancel()

        profileCall = gitHubApi.getUserInfo()
        profileCall?.enqueue(object : Callback<Profile> {
            override fun onFailure(call: Call<Profile>, throwable: Throwable) {
                return resultCallback(DataWrapperForErrorHanding(throwable))
            }

            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                response.body()?.let { profile ->
                    resultCallback(DataWrapperForErrorHanding(profile))
                } ?: resultCallback(DataWrapperForErrorHanding(IllegalStateException("Loaded body is empty!")))
            }
        })

        return configureDismisser(profileCall)
    }

    override fun getProjectRepositories(
        resultCallback: (dataWrapper: DataWrapperForErrorHanding<List<ProjectRepository>>) -> Unit
    ): DataDownloader.Dismisser {
        repositoriesCall?.cancel()

        repositoriesCall = gitHubApi.getRepositories()
        repositoriesCall?.enqueue(object : Callback<List<ProjectRepository>> {
            override fun onFailure(call: Call<List<ProjectRepository>>, throwable: Throwable) =
                resultCallback(DataWrapperForErrorHanding(throwable))

            override fun onResponse(call: Call<List<ProjectRepository>>, response: Response<List<ProjectRepository>>) {
                response.body()?.let { repositories ->
                    resultCallback(DataWrapperForErrorHanding(repositories))
                } ?: resultCallback(DataWrapperForErrorHanding(IllegalStateException("Loaded body is empty!")))
            }
        })

        return configureDismisser(repositoriesCall)
    }

    private fun <T> configureDismisser(call: Call<T>?): DataDownloader.Dismisser {
        return object : DataDownloader.Dismisser {
            override fun dismiss() {
                call?.cancel()
            }
        }
    }

}
