package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.castOnError
import de.bitb.pantryplaner.core.misc.formatDateNow
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Location
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
        val user = localDB.getUser()
        return remoteDB.deleteLocation(user,location)
    }

    suspend fun saveLocations(locations: List<Location>): Resource<Unit> {
        return remoteDB.saveLocations(localDB.getUser(), locations)
    }
}
