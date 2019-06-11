package com.github.andreylitvintsev.profilefetcher.repository


class DataWrapperForErrorHanding<T>(val fetchedData: T?, val throwable: Throwable?) {

    constructor(fetchedData: T) : this(fetchedData, null)

    constructor(throwable: Throwable) : this(null, throwable)

    inline fun handle(
        crossinline onSuccess: (data: T) -> Unit = {},
        crossinline onError: (throwable: Throwable) ->  Unit = {}
    ) {
        when {
            fetchedData != null -> onSuccess(fetchedData)
            throwable != null -> onError(throwable)
        }
    }

}
