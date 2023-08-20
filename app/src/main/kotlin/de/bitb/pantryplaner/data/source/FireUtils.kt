package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

inline fun <reified T> getOwnedOrShared(
    userId: String,
    ids: List<String>?,
    ownerCollection: (String) -> Query,
    sharedCollection: (String) -> Query,
): Flow<Resource<List<T>>> {
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
            .map { it.toObjects(T::class.java) }
        val sharedFlow = sharedQuery.snapshots()
            .map { it.toObjects(T::class.java) }
        ownerFlow.combine(sharedFlow) { owner, shared ->
            Resource.Success(listOf(*owner.toTypedArray(), *shared.toTypedArray()))
        }
    } catch (e: Exception) {
        MutableStateFlow(e.asResourceError(emptyList()))
    }
}