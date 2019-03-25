package com.github.andreylitvintsev.profilefetcher.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.github.andreylitvintsev.profilefetcher.DatabaseProvider
import com.github.andreylitvintsev.profilefetcher.PersistentDataRepository
import com.github.andreylitvintsev.profilefetcher.repository.local.ProjectRepositoryDao
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository


class LocalDataRepository(databaseProvider: DatabaseProvider) : PersistentDataRepository {

    private val projectRepositoryDao: ProjectRepositoryDao

    init {
        with(databaseProvider.provideDatabase()) {
            projectRepositoryDao = repositoryDao()
        }
    }

    override fun getProfile(): LiveData<DataWrapperForErrorHanding<Profile>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getProjectRepositories(): LiveData<DataWrapperForErrorHanding<List<ProjectRepository>>> {
        return Transformations.map(projectRepositoryDao.getAll()) { DataWrapperForErrorHanding(it) }
    }

    override fun upsertProjectRepositories(projectRepositories: List<ProjectRepository>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateProfile(profile: Profile) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
