package com.github.andreylitvintsev.profilefetcher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.github.andreylitvintsev.profilefetcher.MoshiProvider
import com.github.andreylitvintsev.profilefetcher.OkHttpClientProvider
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding


class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val moshiProvider = application as MoshiProvider
    private val okHttpClientProvider = application as OkHttpClientProvider

    private var authLiveData: LiveData<DataWrapperForErrorHanding<String>>? = null
    private var code: String? = null
    private var state: String? = null

    fun getAuthToken(code: String, state: String): LiveData<DataWrapperForErrorHanding<String>> {
        if (code != this.code && state != this.state) {
            authLiveData = AuthLiveData(code, state, moshiProvider, okHttpClientProvider)
        }
        return authLiveData!!
    }

    fun tryRestoreAuthToken(): LiveData<DataWrapperForErrorHanding<String>>? {
        return authLiveData
    }

}
