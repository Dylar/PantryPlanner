package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<Resource<Settings>>
    suspend fun saveSettings(settings: Settings): Resource<Unit>
}

class SettingsRepositoryImpl(
    private val remoteService: RemoteService,
    private val localDB: LocalDatabase,
) : SettingsRepository {

    override fun getSettings(): Flow<Resource<Settings>> {
        return remoteService.getSettings(localDB.getUser())
    }

    override suspend fun saveSettings(settings: Settings): Resource<Unit> {
        return tryIt { remoteService.saveSettings(settings) }
    }

}
