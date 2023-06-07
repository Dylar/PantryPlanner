package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Item
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreService(
    private val firestore: FirebaseFirestore,
) : ItemRemoteDao {

    private val itemCollection
        get() = firestore.collection("items")

    //TODO maybe flow in resource ... or whatever
    override fun getItems(): Flow<Resource<List<Item>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null
        try {
            snapshotStateListener = itemCollection
//                .orderBy("timestamp") TODO order by checked
//                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val notes = snapshot.toObjects(Item::class.java)
                        Resource.Success(notes)
                    } else {
                        Resource.Error(e?.cause!!) //TODO crash haha
                    }
                    trySend(response)
                }
        } catch (e: Exception) {
            trySend(Resource.Error(e))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }


    override suspend fun saveItem(item: Item): Resource<Unit> {
        return tryIt {
            val querySnapshot = itemCollection
                .whereEqualTo("name", item.name)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                itemCollection.add(item).await()
            } else {
                val documentId = querySnapshot.documents.first().id
                itemCollection.document(documentId).set(item, SetOptions.merge()).await()
            }
            Resource.Success()
        }
    }

    override suspend fun removeItem(item: Item): Resource<Unit> {
        return tryIt {
            val querySnapshot = itemCollection
                .whereEqualTo("name", item.name)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentId = querySnapshot.documents.first().id
                itemCollection.document(documentId).delete().await()
            }
            Resource.Success()
        }
    }

}