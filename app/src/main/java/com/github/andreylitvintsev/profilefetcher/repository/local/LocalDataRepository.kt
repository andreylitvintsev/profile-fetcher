package com.github.andreylitvintsev.profilefetcher.repository.local

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.github.andreylitvintsev.profilefetcher.DatabaseProvider
import com.github.andreylitvintsev.profilefetcher.repository.DataWrapperForErrorHanding
import com.github.andreylitvintsev.profilefetcher.repository.PersistentDataRepository
import com.github.andreylitvintsev.profilefetcher.repository.model.Profile
import com.github.andreylitvintsev.profilefetcher.repository.model.ProjectRepository
import com.github.andreylitvintsev.profilefetcher.viewmodel.Event


class LocalDataRepository(databaseProvider: DatabaseProvider) :
    PersistentDataRepository {

    private val profileDao: ProfileDao
    private val projectRepositoryDao: ProjectRepositoryDao

    init {
        with(databaseProvider.provideDatabase()) {
            profileDao = profileDao()
            projectRepositoryDao = projectRepositoryDao()
        }
    }

    override fun getProfile(): LiveData<Event<DataWrapperForErrorHanding<Profile>>> {
        Log.d("HOT", "HOT download")
        return Transformations.map(profileDao.get()) {
            Event(DataWrapperForErrorHanding(it))
        }
    }

    override fun getProjectRepositories(): LiveData<Event<DataWrapperForErrorHanding<List<ProjectRepository>>>> {
        return Transformations.map(projectRepositoryDao.getAll()) {
            Event(DataWrapperForErrorHanding(it))
        }
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

    override fun reset() = Unit // Do nothing :C

}

private class UpsertAsyncTask(private val body: () -> Unit) : AsyncTask<Void?, Void?, Void?>() {

    override fun doInBackground(vararg params: Void?): Void? {
        body.invoke()
        return null
    }

}
