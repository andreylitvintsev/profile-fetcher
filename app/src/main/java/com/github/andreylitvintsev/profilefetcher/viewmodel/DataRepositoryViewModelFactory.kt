package com.github.andreylitvintsev.profilefetcher.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class DataRepositoryViewModelFactory(
    private val authToken: String,
    private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST") // :C
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DataRepositoryViewModel(authToken, application) as T
    }

}
