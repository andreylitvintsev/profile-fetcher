package com.github.andreylitvintsev.profilefetcher

import android.app.Application
import androidx.room.Room
import com.facebook.stetho.Stetho
import com.github.andreylitvintsev.profilefetcher.repository.local.AppDatabase
import com.github.andreylitvintsev.profilefetcher.repository.remote.ReducedProfileAdapter
import com.github.andreylitvintsev.profilefetcher.repository.remote.ReducedRepositoryAdapter
import com.squareup.leakcanary.LeakCanary
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


interface DatabaseProvider {
    fun provideDatabase(): AppDatabase
}

interface MoshiProvider {
    fun provideMoshi(): Moshi
}

interface OkHttpClientProvider {
    fun provideOkHttp(): OkHttpClient
}

class InjectorApplication : Application(), DatabaseProvider, MoshiProvider, OkHttpClientProvider {

    private lateinit var database: AppDatabase
    private lateinit var moshi: Moshi
    private lateinit var okHttpClient: OkHttpClient

    override fun provideDatabase(): AppDatabase = database

    override fun provideMoshi(): Moshi = moshi

    override fun provideOkHttp(): OkHttpClient = okHttpClient

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }

        initDatabase()
        initMoshi()
        initOkHttpClient()
    }

    private fun initDatabase() {
        database = Room.databaseBuilder(this, AppDatabase::class.java, "database").build()
    }

    private fun initMoshi() {
        moshi = Moshi.Builder()
            .add(ReducedProfileAdapter())
            .add(ReducedRepositoryAdapter())
            .build()
    }

    private fun initOkHttpClient() {
        okHttpClient = OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    this.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                }
            }
            .build()
    }

}
