package com.github.andreylitvintsev.profilefetcher.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ProjectRepository(
    @PrimaryKey val id: Long,
    val name: String,
    val isPrivate: Boolean,
    val url: String
)
