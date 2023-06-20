package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Checklist
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreCheckService(
    private val firestore: FirebaseFirestore,
) : CheckRemoteDao {

    private val checkCollection
        get() = firestore
            .collection("stage")
            .document(BuildConfig.FLAVOR)
            .collection("checklists")

    override fun getCheckLists(): Flow<Resource<List<Checklist>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null
        try {
            snapshotStateListener = checkCollection
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val docs = snapshot.toObjects(Checklist::class.java)
                        Resource.Success(docs)
                    } else {
                        Resource.Error(e?.cause!!) //TODO crash? haha
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

    override suspend fun saveChecklist(check: Checklist): Resource<Unit> {
        return tryIt {
            checkCollection
                .whereEqualTo("uuid", check.uuid)
                .get().await()
                .documents.first()
                .reference.set(check)
            Resource.Success()
        }
    }

    override suspend fun addChecklist(check: Checklist): Resource<Boolean> {
        return tryIt {
            val querySnapshot = checkCollection
                .whereEqualTo("uuid", check.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                checkCollection.add(check).await()
                Resource.Success(true)
            } else {
                Resource.Success(false)
            }
        }
    }

    override suspend fun removeChecklist(check: Checklist): Resource<Boolean> {
        return tryIt {
            val querySnapshot = checkCollection
                .whereEqualTo("uuid", check.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                Resource.Success(false)
            } else {
                val documentId = querySnapshot.documents.first().id
                checkCollection.document(documentId).delete().await()
                Resource.Success(true)
            }
        }
    }

}