package com.github.andreylitvintsev.profilefetcher.repository.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile


@Dao
interface ProfileDao {

    @Query("SELECT * FROM profile")
    fun get(): LiveData<Profile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(profile: Profile)

}
