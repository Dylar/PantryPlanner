package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.FirebaseFirestore
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Checklist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FireCheckService(
    firestore: FirebaseFirestore,
) : CheckRemoteDao {

    private val checkCollection = firestore
        .collection("stage")
        .document(BuildConfig.FLAVOR)
        .collection("checklists")

    private fun ownerCollection(id: String) =
        checkCollection.whereEqualTo("creator", id)

    private fun sharedCollection(id: String) =
        checkCollection.whereArrayContains("sharedWith", id)

    override fun getCheckLists(
        userId: String,
        ids: List<String>?,
    ): Flow<Result<List<Checklist>>> {
        return getOwnedOrShared(userId, ids, ::ownerCollection, ::sharedCollection)
    }

    override suspend fun saveChecklist(check: Checklist): Result<Unit> {
        return tryIt {
            checkCollection
                .whereEqualTo("uuid", check.uuid)
                .get().await()
                .documents.first()
                .reference.set(check)
            Result.Success()
        }
    }

    override suspend fun addChecklist(check: Checklist): Result<Boolean> {
        return tryIt {
            val querySnapshot = checkCollection
                .whereEqualTo("uuid", check.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                checkCollection.add(check).await()
                Result.Success(true)
            } else {
                Result.Success(false)
            }
        }
    }

    override suspend fun deleteChecklist(check: Checklist): Result<Boolean> {
        return tryIt {
            val querySnapshot = checkCollection
                .whereEqualTo("uuid", check.uuid)
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