package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FireItemService(
    private val firestore: FirebaseFirestore,
) : ItemRemoteDao {

    private val itemCollection = firestore
        .collection("stage")
        .document(BuildConfig.FLAVOR)
        .collection("items")

    private fun ownerCollection(id: String) =
        itemCollection.whereEqualTo("creator", id)

    private fun sharedCollection(id: String) =
        itemCollection.whereArrayContains("sharedWith", id)

    override fun getItems(userId: String, ids: List<String>?): Flow<Resource<List<Item>>> {
        return try {
            if (ids?.isEmpty() == true) {
                return MutableStateFlow(Resource.Success(emptyList()))
            }

            val ownerQuery =
                if (ids == null) ownerCollection(userId)
                else ownerCollection(userId).whereIn("uuid", ids)

            val sharedQuery =
                if (ids == null) sharedCollection(userId)
                else sharedCollection(userId).whereIn("uuid", ids)

            val ownerFlow = ownerQuery.snapshots()
                .map { it.toObjects(Item::class.java) }
            val sharedFlow = sharedQuery.snapshots()
                .map { it.toObjects(Item::class.java) }
            ownerFlow.combine(sharedFlow) { owner, shared ->
                Resource.Success(listOf(*owner.toTypedArray(), *shared.toTypedArray()))
            }
        } catch (e: Exception) {
            MutableStateFlow(e.asResourceError(emptyList()))
        }
    }

    override suspend fun saveItems(userId: String, items: List<Item>): Resource<Unit> {
        return tryIt {
            firestore.batch().apply {
                val itemCollection = ownerCollection(userId)
                itemCollection
                    .whereIn("uuid", items.map { it.uuid })
                    .get().await().documents
                    .forEach { snap ->
                        val uuid = snap.data?.get("uuid") ?: ""
                        set(snap.reference, items.first { it.uuid == uuid })
                    }
                commit()
            }
            Resource.Success()
        }
    }

    override suspend fun addItem(item: Item): Resource<Boolean> {
        return tryIt {
            val querySnapshot = itemCollection
                .whereEqualTo("uuid", item.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                itemCollection.add(item).await()
                Resource.Success(true)
            } else {
                Resource.Success(false)
            }
        }
    }

    override suspend fun removeItem(userId: String, item: Item): Resource<Boolean> {
        return tryIt {
            val itemCollection = ownerCollection(userId)
            val querySnapshot = itemCollection
                .whereEqualTo("uuid", item.uuid)
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