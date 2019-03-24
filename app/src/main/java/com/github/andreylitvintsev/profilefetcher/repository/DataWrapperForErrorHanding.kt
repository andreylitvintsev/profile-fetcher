package com.github.andreylitvintsev.profilefetcher.repository


class DataWrapperForErrorHanding<T>(val fetchedData: T?, val throwable: Throwable?) {

    constructor(fetchedData: T) : this(fetchedData, null)

    constructor(throwable: Throwable) : this(null, throwable)

}
