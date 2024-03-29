package de.bitb.pantryplaner.data.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreItemService(
    private val firestore: FirebaseFirestore,
    private val fireAuth: FirebaseAuth
) : ItemRemoteDao, UserRemoteDao {

    private val itemCollection
        get() = firestore
            .collection("stage")
            .document(BuildConfig.FLAVOR)
            .collection("items")

    override suspend fun loginUser(): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                fireAuth.signInAnonymously().await()
                Resource.Success(true)
            },
        )
    }

    override fun getItems(ids: List<String>?): Flow<Resource<List<Item>>> {
        if (ids?.isEmpty() == true) {
            return MutableStateFlow(Resource.Success(emptyList()))
        }
        val ref =
            if (ids == null) itemCollection
            else itemCollection.whereIn("uuid", ids)

        return ref.snapshots().map {
            tryIt {
                val items = it.toObjects(Item::class.java)
                Resource.Success(items)
            }
        }
    }

    override suspend fun saveItems(items: List<Item>): Resource<Unit> {
        return tryIt {
            firestore.batch().apply {
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

    override suspend fun removeItem(item: Item): Resource<Boolean> {
        return tryIt {
            val querySnapshot = itemCollection
                .whereEqualTo("uuid", item.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                Resource.Success(false)
            } else {
                val documentId = querySnapshot.documents.first().id
                itemCollection.document(documentId).delete().await()
                Resource.Success(true)
            }
        }
    }

}