package com.github.andreylitvintsev.profilefetcher

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


// TODO: https://github.com/square/okhttp/wiki/Recipes
class MainActivity : AppCompatActivity(), NewIntentListenerHolder {

    private val newIntentListeners = mutableListOf<OnNewIntentListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("TAG", "onCreate")

        if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
//            supportFragmentManager.beginTransaction().add(android.R.id.content, LoadingStubFragment()).commit()
            supportFragmentManager.beginTransaction().add(android.R.id.content, AuthFragment()).commit()
        }
    }

    override fun addOnNewIntentListener(onNewIntentListener: OnNewIntentListener) {
        newIntentListeners.add(onNewIntentListener)
    }

    override fun removeListener(onNewIntentListener: OnNewIntentListener) {
        newIntentListeners.remove(onNewIntentListener)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        newIntentListeners.forEach { it.onNewIntent(intent) }
        newIntentListeners.clear()
    }

}
