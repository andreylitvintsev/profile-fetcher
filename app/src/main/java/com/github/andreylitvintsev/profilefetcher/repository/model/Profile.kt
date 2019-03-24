package com.github.andreylitvintsev.profilefetcher.repository.model


data class Profile(
    val avatarUrl: String,
    val login: String,
    val name: String,
    val location: String,
    val url: String
)
