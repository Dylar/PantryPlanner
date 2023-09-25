package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.formatDateNow
import de.bitb.pantryplaner.data.model.Location
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow

class LocationRepository(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) {

    fun getLocations(
        userId: String? = null,
    ): Flow<Resource<List<Location>>> {
        return remoteDB.getLocations(userId ?: localDB.getUser())
    }

    suspend fun addLocation(location: Location): Resource<Boolean> {
        val now = formatDateNow()
        val user = localDB.getUser()
        return remoteDB.addLocation(location.copy(creator = user, createdAt = now))
    }

    suspend fun deleteLocation(location: Location): Resource<Boolean> {
        return remoteDB.deleteLocation(location)
    }

    suspend fun saveLocations(locations: List<Location>): Resource<Unit> {
        return remoteDB.saveLocations(locations)
    }
}
