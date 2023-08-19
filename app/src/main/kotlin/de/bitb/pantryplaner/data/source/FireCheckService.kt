package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Checklist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FireCheckService(
    firestore: FirebaseFirestore,
) : CheckRemoteDao {

    private val checkCollection = firestore
        .collection("stage")
        .document(BuildConfig.FLAVOR)
        .collection("checklists")

    private fun checkOwnerCollection(id: String) =
        checkCollection.whereEqualTo("creator", id)

    private fun checkSharedCollection(id: String) =
        checkCollection.whereArrayContains("sharedWith", id)

    override fun getCheckLists(
        userId: String,
        ids: List<String>?,
    ): Flow<Resource<List<Checklist>>> {
        return try {
            if (ids?.isEmpty() == true) {
                return MutableStateFlow(Resource.Success(emptyList()))
            }

            val ownerQuery =
                if (ids == null) checkOwnerCollection(userId)
                else checkOwnerCollection(userId).whereIn("uuid", ids)

            val sharedQuery =
                if (ids == null) checkSharedCollection(userId)
                else checkSharedCollection(userId).whereIn("uuid", ids)

            val ownerFlow = ownerQuery.snapshots()
                .map { it.toObjects(Checklist::class.java) }
            val sharedFlow = sharedQuery.snapshots()
                .map { it.toObjects(Checklist::class.java) }
            ownerFlow.combine(sharedFlow) { owner, shared ->
                Resource.Success(listOf(*owner.toTypedArray(), *shared.toTypedArray()))
            }
        } catch (e: Exception) {
            MutableStateFlow(e.asResourceError(emptyList()))
        }
    }

    override suspend fun saveChecklist(userId: String, check: Checklist): Resource<Unit> {
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

    override suspend fun removeChecklist(userId: String, check: Checklist): Resource<Boolean> {
        return tryIt {
            val checkCollection = checkOwnerCollection(userId)
            val querySnapshot = checkCollection
                .whereEqualTo("uuid", check.uuid)
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