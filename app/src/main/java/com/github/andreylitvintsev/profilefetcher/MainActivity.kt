package com.github.andreylitvintsev.profilefetcher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.github.andreylitvintsev.profilefetcher.viewmodel.AuthViewModel
import com.github.andreylitvintsev.profilefetcher.viewmodel.observeDataWrapper


// TODO: https://github.com/square/okhttp/wiki/Recipes
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("TAG", "onCreate")

        if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
//            supportFragmentManager.beginTransaction().add(android.R.id.content, LoadingStubFragment()).commit()
            supportFragmentManager.beginTransaction().add(android.R.id.content, AuthFragment()).commit()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri = Uri.parse(intent!!.dataString) // FIXME
        if (uri != null) {
            ViewModelProviders.of(this)
                .get(AuthViewModel::class.java)
                .getAuthToken(
                    uri.getQueryParameter("code")!!,
                    uri.getQueryParameter("state")!!
                )
                .observe(this, observeDataWrapper(
                    onSuccess = {
                        Log.d("TAG", "onSuccess: $it")
                    },
                    onError = {
                        Log.d("TAG", "onSuccess: $it")
                    }
                ))
        }
    }
}
