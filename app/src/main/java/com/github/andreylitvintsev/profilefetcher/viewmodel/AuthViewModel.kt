package com.github.andreylitvintsev.profilefetcher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.github.andreylitvintsev.profilefetcher.MoshiProvider
import com.github.andreylitvintsev.profilefetcher.OkHttpClientProvider
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding


class AuthViewModel(application: Application) : AndroidViewModel(application) {

    val moshiProvider = application as MoshiProvider
    val okHttpClientProvider = application as OkHttpClientProvider

    fun getAuthToken(code: String, state: String): LiveData<DataWrapperForErrorHanding<String>> {
        return AuthLiveData(code, state, moshiProvider, okHttpClientProvider)
    }

}
