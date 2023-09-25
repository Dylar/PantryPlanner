package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.FirebaseFirestore
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FireLocationService(
    private val firestore: FirebaseFirestore,
) : LocationRemoteDao {

    private val collection = firestore
        .collection("stage")
        .document(BuildConfig.FLAVOR)
        .collection("locations")

    private fun ownerCollection(id: String) =
        collection.whereEqualTo("creator", id)

    private fun sharedCollection(id: String) =
        collection.whereArrayContains("sharedWith", id)

    override fun getLocations(
        userId: String,
    ): Flow<Resource<List<Location>>> {
        return getOwnedOrShared(
            userId,
            ownerCollection = ::ownerCollection,
            sharedCollection = ::sharedCollection,
        )
    }

    override suspend fun saveLocations(locations: List<Location>): Resource<Unit> {
        return tryIt {
            firestore.batch().apply {
                collection
                    .whereIn("uuid", locations.map { it.uuid })
                    .get().await().documents
                    .forEach { snap ->
                        val uuid = snap.data?.get("uuid") ?: ""
                        set(snap.reference, locations.first { it.uuid == uuid })
                    }
                commit()
            }
            Resource.Success()
        }
    }

    override suspend fun addLocation(location: Location): Resource<Boolean> {
        return tryIt {
            val querySnapshot = collection
                .whereEqualTo("uuid", location.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                collection.add(location).await()
                Resource.Success(true)
            } else {
                Resource.Success(false)
            }
        }
    }

    override suspend fun deleteLocation(location: Location): Resource<Boolean> {
        return tryIt {
            val querySnapshot = collection
                .whereEqualTo("uuid", location.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                Resource.Success(false)
            } else {
                querySnapshot.documents.first().reference.delete().await()
                Resource.Success(true)
            }
        }
    }

}