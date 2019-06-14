package com.github.andreylitvintsev.profilefetcher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

// TODO: попробовать перенести обертку в Event
// TODO: попробовать перенести убрать ленивую иництализацию
// TODO: убрать утечки в памяти
// TODO: разобраться с "notok"
// TODO: разлогин
// TODO: сделать отображение загрузки
// TODO: немного прибраться в коде
// TODO: поведение при timeout

// TODO: https://github.com/square/okhttp/wiki/Recipes
class MainActivity : AppCompatActivity(), NewIntentListenerHolder, DetectAuthTokenListener {

    companion object {
        const val AUTH_TOKEN_KEY = "AUTH_TOKEN"
    }

    private val newIntentListeners = mutableListOf<OnNewIntentListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authToken = getPreferences(Activity.MODE_PRIVATE).getString(AUTH_TOKEN_KEY, "")

        Log.d("TOKEN", "old token $authToken")


        if (authToken.isEmpty()) requestToken()
        else onDetectAuthToken(authToken)
    }

    override fun addOnNewIntentListener(onNewIntentListener: OnNewIntentListener) {
        newIntentListeners.add(onNewIntentListener)
    }

    override fun removeListener(onNewIntentListener: OnNewIntentListener) {
        newIntentListeners.remove(onNewIntentListener)
    }

    override fun onNewIntent(intent: Intent?) {
        Log.d("TAG", "onNewIntent")
        super.onNewIntent(intent)
        newIntentListeners.forEach { it.onNewIntent(intent) }
        newIntentListeners.clear()
    }

    override fun onDetectAuthToken(token: String) {
        supportFragmentManager.beginTransaction()
            .safetyReplace(android.R.id.content, LoadingStubFragment.instantiate(token))
            ?.commit()
    }

    private fun requestToken() {
        supportFragmentManager.beginTransaction()
            .safetyReplace(android.R.id.content, AuthFragment())
            ?.commit()
    }

    private fun FragmentTransaction.safetyReplace(@IdRes containerViewId: Int, fragment: Fragment): FragmentTransaction? {
        val findedFragment = supportFragmentManager.findFragmentById(android.R.id.content)
        if (findedFragment == null || findedFragment::class.java != fragment::class.java) {
            return this.replace(containerViewId, fragment)
        }
        return null
    }

}
