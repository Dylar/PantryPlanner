package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val remoteService: RemoteService,
    private val localDB: LocalDatabase,
) {

    suspend fun getAppVersion(): Result<String> {
        return remoteService.getAppVersion()
    }

    suspend fun getAppDownloadURL(): Result<String> {
        return remoteService.getAppDownloadURL()
    }

    fun getSettings(): Flow<Result<Settings>> {
        return remoteService.getSettings(localDB.getUser())
    }

    suspend fun saveSettings(settings: Settings): Result<Unit> {
        return tryIt { remoteService.saveSettings(settings) }
    }

}
