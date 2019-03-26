package com.github.andreylitvintsev.profilefetcher.viewmodel

import androidx.lifecycle.Observer
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import java.lang.IllegalStateException

abstract class RepositoryResultObserver<T> : Observer<DataWrapperForErrorHanding<T>> {

    override fun onChanged(dataWrapper: DataWrapperForErrorHanding<T>?) {
        when {
            dataWrapper?.fetchedData != null -> onSuccess(dataWrapper.fetchedData)
            dataWrapper?.throwable != null -> onError(dataWrapper.throwable)
        }
    }

    abstract fun onSuccess(data: T)

    abstract fun onError(throwable: Throwable)

}


inline fun <T> observeDataWrapper(
    crossinline onSuccess: (data: T) -> Unit = {},
    crossinline onError: (throwable: Throwable) -> Unit = {}
): Observer<DataWrapperForErrorHanding<T>> {
    return object : RepositoryResultObserver<T>() {
        override fun onSuccess(data: T) = onSuccess(data)
        override fun onError(throwable: Throwable) = onError(throwable)
    }
}
