package com.github.andreylitvintsev.profilefetcher.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Profile(
    @PrimaryKey val id: Long,
    val avatarUrl: String,
    val login: String,
    val name: String,
    val location: String,
    val url: String
)
