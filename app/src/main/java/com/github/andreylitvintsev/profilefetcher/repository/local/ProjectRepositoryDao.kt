package com.github.andreylitvintsev.profilefetcher.repository.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository


@Dao
interface ProjectRepositoryDao {
    @Query("SELECT * FROM projectrepository")
    fun getAll(): LiveData<List<ProjectRepository>>

    @Insert
    fun insertAll(projectRepositories: List<ProjectRepository>)
}
