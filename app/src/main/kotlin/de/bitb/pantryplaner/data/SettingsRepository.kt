package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val remoteService: RemoteService,
    private val localDB: LocalDatabase,
) {

    suspend fun getAppVersion(): Resource<String> {
        return remoteService.getAppVersion()
    }

    suspend fun getAppDownloadURL(): Resource<String> {
        return remoteService.getAppDownloadURL()
    }

    fun getSettings(): Flow<Resource<Settings>> {
        return remoteService.getSettings(localDB.getUser())
    }

    suspend fun saveSettings(settings: Settings): Resource<Unit> {
        return tryIt { remoteService.saveSettings(settings) }
    }

}
