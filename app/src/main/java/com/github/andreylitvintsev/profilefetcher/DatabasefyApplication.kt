package com.github.andreylitvintsev.profilefetcher

import android.app.Application
import androidx.room.Room
import com.facebook.stetho.Stetho
import com.github.andreylitvintsev.profilefetcher.repository.local.AppDatabase
import com.squareup.leakcanary.LeakCanary


interface DatabaseProvider {
    fun provideDatabase(): AppDatabase
}

class DatabasefyApplication : Application(), DatabaseProvider {

    private lateinit var database: AppDatabase

    override fun provideDatabase(): AppDatabase = database

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
        database = Room.databaseBuilder(this, AppDatabase::class.java, "database").build()
    }

}
