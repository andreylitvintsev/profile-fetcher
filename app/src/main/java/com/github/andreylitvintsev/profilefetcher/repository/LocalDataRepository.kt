package com.github.andreylitvintsev.profilefetcher.repository

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.github.andreylitvintsev.profilefetcher.DatabaseProvider
import com.github.andreylitvintsev.profilefetcher.PersistentDataRepository
import com.github.andreylitvintsev.profilefetcher.repository.local.ProfileDao
import com.github.andreylitvintsev.profilefetcher.repository.local.ProjectRepositoryDao
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository


class LocalDataRepository(databaseProvider: DatabaseProvider) : PersistentDataRepository {

    private val profileDao: ProfileDao
    private val projectRepositoryDao: ProjectRepositoryDao

    init {
        with(databaseProvider.provideDatabase()) {
            profileDao = profileDao()
            projectRepositoryDao = projectRepositoryDao()
        }
    }

    override fun getProfile(): LiveData<DataWrapperForErrorHanding<Profile>> {
        return Transformations.map(profileDao.get()) { DataWrapperForErrorHanding(it) }
    }

    override fun getProjectRepositories(): LiveData<DataWrapperForErrorHanding<List<ProjectRepository>>> {
        return Transformations.map(projectRepositoryDao.getAll()) { DataWrapperForErrorHanding(it) }
    }

    override fun upsertProjectRepositories(projectRepositories: List<ProjectRepository>) {
        UpsertAsyncTask {
            projectRepositoryDao.insertAll(projectRepositories)
        }.execute()
    }

    override fun updateProfile(profile: Profile) {
        UpsertAsyncTask {
            profileDao.upsert(profile)
        }.execute()
    }

    override fun reload() = Unit // Do nothing :(

}

private class UpsertAsyncTask(private val body: () -> Unit) : AsyncTask<Void?, Void?, Void?>() {

    override fun doInBackground(vararg params: Void?): Void? {
        body.invoke()
        return null
    }

}
