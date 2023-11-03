package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

inline fun <reified T> getOwnedOrShared(
    userId: String,
    ids: List<String>? = null,
    ownerCollection: (String) -> Query,
    sharedCollection: (String) -> Query,
): Flow<Result<List<T>>> {
    return try {
        if (ids?.isEmpty() == true) {
            return MutableStateFlow(Result.Success(emptyList()))
        }

        val ownerQuery =
            if (ids == null) ownerCollection(userId)
            else ownerCollection(userId).whereIn("uuid", ids)

        val sharedQuery =
            if (ids == null) sharedCollection(userId)
            else sharedCollection(userId).whereIn("uuid", ids)

        combine(
            ownerQuery.snapshots().map { it.toObjects(T::class.java) },
            sharedQuery.snapshots().map { it.toObjects(T::class.java) }
        ) { owner, shared ->
            Result.Success(setOf(*owner.toTypedArray(), *shared.toTypedArray()).toList())
        }
    } catch (e: Exception) {
        MutableStateFlow(e.asError(emptyList()))
    }
}