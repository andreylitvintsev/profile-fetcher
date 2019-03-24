package com.github.andreylitvintsev.profilefetcher.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository


@Database(entities = [ProjectRepository::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao
}
