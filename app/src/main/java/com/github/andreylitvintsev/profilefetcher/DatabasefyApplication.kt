package com.github.andreylitvintsev.profilefetcher

import android.app.Application
import androidx.room.Room
import com.facebook.stetho.Stetho
import com.github.andreylitvintsev.profilefetcher.repository.local.AppDatabase


interface DatabaseProvider {
    fun provideDatabase(): AppDatabase
}

class DatabasefyApplication : Application(), DatabaseProvider {

    private lateinit var database: AppDatabase

    override fun provideDatabase(): AppDatabase = database

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
        database = Room.databaseBuilder(this, AppDatabase::class.java, "database").build()
    }

}
