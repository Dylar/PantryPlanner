package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.FirebaseFirestore
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FireItemService(
    private val firestore: FirebaseFirestore,
) : ItemRemoteDao {

    private val collection = firestore
        .collection("stage")
        .document(BuildConfig.FLAVOR)
        .collection("items")

    private fun ownerCollection(id: String) =
        collection.whereEqualTo("creator", id)

    private fun sharedCollection(id: String) =
        collection.whereArrayContains("sharedWith", id)

    override fun getItems(
        ids: List<String>,
    ): Flow<Result<List<Item>>> {
        if (ids.isEmpty()) {
            return flowOf(Result.Success(emptyList()))
        }

        return chunkQuery<Item>(ids) {
            collection.whereIn("uuid", ids)
        }.map { Result.Success(it) }
    }

    override fun getItems(
        userId: String,
        ids: List<String>?,
    ): Flow<Result<List<Item>>> {
        return getOwnedOrShared(userId, ids, ::ownerCollection, ::sharedCollection)
    }

    override suspend fun saveItems(items: List<Item>): Result<Unit> {
        return tryIt {
            firestore.batch().apply {
                items.chunked(10).forEach { chunk ->
                    collection
                        .whereIn("uuid", chunk.map { it.uuid })
                        .get().await().documents
                        .forEach { snap ->
                            val uuid = snap.data?.get("uuid") ?: ""
                            set(snap.reference, chunk.first { it.uuid == uuid })
                        }
                }
                commit()
            }
            Result.Success()
        }
    }

    override suspend fun addItem(item: Item): Result<Boolean> {
        return tryIt {
            val querySnapshot = collection
                .whereEqualTo("uuid", item.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                collection.add(item).await()
                Result.Success(true)
            } else {
                Result.Success(false)
            }
        }
    }

    override suspend fun deleteItem(item: Item): Result<Boolean> {
        return tryIt {
            val querySnapshot = collection
                .whereEqualTo("uuid", item.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                Result.Success(false)
            } else {
                querySnapshot.documents.first().reference.delete().await()
                Result.Success(true)
            }
        }
    }

}