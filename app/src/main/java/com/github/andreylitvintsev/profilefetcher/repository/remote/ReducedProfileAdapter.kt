package com.github.andreylitvintsev.profilefetcher.repository.remote

import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader


class ReducedProfileAdapter {

    companion object {
        const val FIELD_AVATAR_URL = "avatar_url"
        const val FIELD_LOGIN = "login"
        const val FIELD_NAME = "name"
        const val FIELD_LOCATION = "location"
        const val FIELD_PROFILE_URL = "html_url"
    }

    @FromJson
    fun fromJson(jsonReader: JsonReader): Profile {
        var login = ""
        var name = ""
        var location = ""
        var avatarUrl = ""
        var url = ""

        jsonReader.beginObject()
        while (jsonReader.hasNext()) {
            when (jsonReader.nextName()) {
                FIELD_AVATAR_URL -> avatarUrl = jsonReader.nextString()
                FIELD_LOGIN -> login = jsonReader.nextString()
                FIELD_NAME -> name = jsonReader.nextString()
                FIELD_LOCATION -> location = jsonReader.nextString()
                FIELD_PROFILE_URL -> url = jsonReader.nextString()
                else -> jsonReader.skipValue()
            }
        }
        jsonReader.endObject()

        return Profile(
            avatarUrl,
            login,
            name,
            location,
            url
        )
    }

}
