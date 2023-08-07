package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Checklist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FireCheckService(
    private val firestore: FirebaseFirestore,
) : CheckRemoteDao {

    private val checkCollection
        get() = firestore
            .collection("stage")
            .document(BuildConfig.FLAVOR)
            .collection("checklists")

    override fun getCheckLists(ids: List<String>?): Flow<Resource<List<Checklist>>> {
        if (ids?.isEmpty() == true) {
            return MutableStateFlow(Resource.Success(emptyList()))
        }
        val ref =
            if (ids == null) checkCollection
            else checkCollection.whereIn("uuid", ids)

        return ref.snapshots().map {
            tryIt {
                val docs = it.toObjects(Checklist::class.java)
                Resource.Success(docs)
            }
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