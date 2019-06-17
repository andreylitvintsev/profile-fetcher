package com.github.andreylitvintsev.profilefetcher.viewmodel

import android.util.Log


open class Event<out T>(private val content: T) {

    init {
        Log.d("TAG", "new event")
    }

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T {
        hasBeenHandled = true
        return content
    }

    fun quietlyPeekContent(): T = content

}
