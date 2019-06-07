package com.github.andreylitvintsev.profilefetcher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.andreylitvintsev.profilefetcher.viewmodel.AuthLiveData
import com.github.andreylitvintsev.profilefetcher.viewmodel.observeDataWrapper
import com.squareup.moshi.Types
import okhttp3.*
import java.io.IOException

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
            AuthLiveData(
                uri.getQueryParameter("code")!!,
                uri.getQueryParameter("state")!!,
                application as MoshiProvider,
                application as OkHttpClientProvider
            ).observe(this, observeDataWrapper(
                onSuccess = {
                    Log.d("TAG", "onSuccess: $it")
                },
                onError = {
                    Log.d("TAG", "onSuccess: $it")
                }
            ))

            // TODO: можно вынести в livedata
//            val request = Request.Builder()
//                .url("https://github.com/login/oauth/access_token?client_id=d43b1ed587684df51150&client_secret=33112468277cd9dfc5a861c2bc035ef4e4b846a6&code=$code&state=$state")
//                .addHeader("Accept", "application/json")
//                .post(RequestBody.create(null, ByteArray(0)))
//                .build()
//
//            okHttpClient.newCall(request).enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    Log.d("TAg", "err")
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    val adapter = moshi.adapter<Map<String, String>>(Types.newParameterizedType(Map::class.java, String::class.java, Object::class.java))
//                    Log.d("TAg", adapter.fromJson(response.body()?.string() ?: "")!!.get("access_token"))
//                }
//            })

        }
    }
}
