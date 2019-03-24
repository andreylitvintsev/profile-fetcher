package com.github.andreylitvintsev.profilefetcher.repository.remote

import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader


class ReducedRepositoryAdapter {

    companion object {
        const val FIELD_ID = "id"
        const val FIELD_NAME = "name"
        const val FIELD_IS_PRIVATE = "private"
        const val FIELD_URL = "html_url"
    }

    @FromJson
    fun fromJson(jsonReader: JsonReader): ProjectRepository {
        var id: Long = -1L
        var name: String = ""
        var isPrivate: Boolean = false
        var url: String = ""

        jsonReader.beginObject()
        while (jsonReader.hasNext()) {
            when(jsonReader.nextName()) {
                FIELD_ID -> id = jsonReader.nextLong()
                FIELD_NAME -> name = jsonReader.nextString()
                FIELD_IS_PRIVATE -> isPrivate = jsonReader.nextBoolean()
                FIELD_URL -> url = jsonReader.nextString()
                else -> jsonReader.skipValue()
            }
        }
        jsonReader.endObject()

        return ProjectRepository(id, name, isPrivate, url)
    }

}
