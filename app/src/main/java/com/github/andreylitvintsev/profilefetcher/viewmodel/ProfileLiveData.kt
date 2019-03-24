package com.github.andreylitvintsev.profilefetcher.viewmodel

import androidx.lifecycle.LiveData
import com.github.andreylitvintsev.profilefetcher.repository.DataRepository
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile


class ProfileLiveData(
    private val dataRepository: DataRepository
) : LiveData<DataWrapperForErrorHanding<Profile>>() {

    private var alreadyLoaded = false

    private lateinit var dismisser: DataRepository.Dismisser

    override fun onInactive() {
        if (!hasObservers()) dismisser.dismiss()
    }

    override fun onActive() {
        if (!alreadyLoaded) {
            dismisser = dataRepository.getProfile(
                resultCallback = { result ->
                    alreadyLoaded = result.throwable == null
                    value = result
                }
            )
        }
    }

}
