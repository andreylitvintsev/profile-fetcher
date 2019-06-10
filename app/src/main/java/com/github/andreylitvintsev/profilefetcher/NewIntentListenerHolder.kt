package com.github.andreylitvintsev.profilefetcher


interface NewIntentListenerHolder {

    fun addOnNewIntentListener(onNewIntentListener: OnNewIntentListener)

    fun removeListener(onNewIntentListener: OnNewIntentListener)

}
