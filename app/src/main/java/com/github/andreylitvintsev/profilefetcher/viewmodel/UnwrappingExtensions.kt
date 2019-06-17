package com.github.andreylitvintsev.profilefetcher.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding


inline fun <T> LiveData<DataWrapperForErrorHanding<T>>.observeWithErrorHandling(
    lifecycleOwner: LifecycleOwner,
    crossinline onSuccess: (data: T) -> Unit = {},
    crossinline onError: (throwable: Throwable) -> Unit = {}
): Observer<DataWrapperForErrorHanding<T>> {

    return this.observeWithResult(lifecycleOwner, Observer { wrappedData ->
        wrappedData.handle(onSuccess, onError)
    })

}


inline fun <T> LiveData<Event<T>>.observeEvent(
    lifecycleOwner: LifecycleOwner,
    crossinline onNewEventContent: (content: T, isNewContent: Boolean) -> Unit
): Observer<Event<T>> {

    return this.observeWithResult(lifecycleOwner, Observer { event ->
        val isNewContent = !event.hasBeenHandled
        onNewEventContent(event.peekContent(), isNewContent)
    })

}


inline fun <T> LiveData<Event<DataWrapperForErrorHanding<T>>>.observeEventWithErrorHandling(
    lifecycleOwner: LifecycleOwner,
    crossinline onSuccess: (data: T, isNewContent: Boolean) -> Unit = { _, _ -> },
    crossinline onError: (throwable: Throwable, isNewContent: Boolean) -> Unit = { _, _ -> }
): Observer<Event<DataWrapperForErrorHanding<T>>> {

    return this.observeWithResult(lifecycleOwner, Observer { event -> // FIXME: проблема в том что всегда будет возвращаться одно и то же значение
        val isNewContent = !event.hasBeenHandled
        event.peekContent().handle(
            onSuccess = {
                onSuccess(it, isNewContent)
            },
            onError = {
                onError(it, isNewContent)
            }
        )
    })

}

inline fun <T> LiveData<T>.observeWithResult(
    lifecycleOwner: LifecycleOwner,
    observer: Observer<T>
): Observer<T> {

    this.observe(lifecycleOwner, observer)
    return observer

}
