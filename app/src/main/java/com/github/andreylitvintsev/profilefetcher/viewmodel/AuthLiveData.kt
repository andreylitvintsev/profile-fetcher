package com.github.andreylitvintsev.profilefetcher.viewmodel

import androidx.lifecycle.LiveData
import com.github.andreylitvintsev.profilefetcher.MoshiProvider
import com.github.andreylitvintsev.profilefetcher.OkHttpClientProvider
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import com.github.andreylitvintsev.profilefetcher.repository.remote.DataDownloader
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import okhttp3.*
import java.io.IOException


class AuthLiveData(
    private val code: String,
    private val state: String,
    private val moshiProvider: MoshiProvider,
    private val okHttpClientProvider: OkHttpClientProvider
) : LiveData<DataWrapperForErrorHanding<String>>() {

    private var alreadyLoaded = false

    private lateinit var dismisser: DataDownloader.Dismisser

    override fun onInactive() {
        if (!hasObservers()) dismisser.dismiss()
    }

    override fun onActive() {
        if (!alreadyLoaded) {
            val request = Request.Builder()
                .url("https://github.com/login/oauth/access_token?client_id=d43b1ed587684df51150&client_secret=33112468277cd9dfc5a861c2bc035ef4e4b846a6&code=$code&state=$state")
                .addHeader("Accept", "application/json")
                .post(RequestBody.create(null, ByteArray(0)))
                .build()

            okHttpClientProvider.provideOkHttp().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    postValue(DataWrapperForErrorHanding(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body()?.string()
                    postValue(handleBody(responseBody))
                    alreadyLoaded = true
                }
            })
        }
    }

    private fun handleBody(responseBody: String?): DataWrapperForErrorHanding<String> {
        if (responseBody == null)
            return DataWrapperForErrorHanding(IllegalStateException("Response body is empty!"))

        val accessToken = generateJsonAdapter().fromJson(responseBody)?.get("access_token")
            ?: return DataWrapperForErrorHanding(IllegalStateException("Response body without 'access_token' field!"))

        return DataWrapperForErrorHanding(accessToken)
    }

    private fun generateJsonAdapter(): JsonAdapter<Map<String, String>> {
        return moshiProvider.provideMoshi()
            .adapter<Map<String, String>>(
                Types.newParameterizedType(
                    Map::class.java,
                    String::class.java, Object::class.java
                )
            )
    }

}

