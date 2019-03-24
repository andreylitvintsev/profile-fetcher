package com.github.andreylitvintsev.profilefetcher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// TODO: https://github.com/square/okhttp/wiki/Recipes
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
            supportFragmentManager.beginTransaction().add(android.R.id.content, LoadingStubFragment()).commit()
        }
    }

}
