package com.github.andreylitvintsev.profilefetcher

import com.github.andreylitvintsev.profilefetcher.repository.DataRepository
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository


interface PersistentDataRepository : DataRepository {
    fun updateProfile(profile: Profile)
    fun upsertProjectRepositories(projectRepositories: List<ProjectRepository>)
}
