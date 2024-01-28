package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

inline fun <reified T> getOwnedOrShared(
    userId: String,
    ids: List<String>? = null,
    crossinline ownerCollection: (String) -> Query,
    crossinline sharedCollection: (String) -> Query,
): Flow<Result<List<T>>> {
    return try {
        if (ids?.isEmpty() == true) {
            return flowOf(Result.Success(emptyList()))
        }

        val ownerQuery =
            chunkQuery<T>(
                ids,
                onNull = { ownerCollection(userId) },
            ) { chunk ->
                ownerCollection(userId).whereIn("uuid", chunk)
            }

        val sharedQuery =
            chunkQuery<T>(
                ids,
                onNull = { sharedCollection(userId) },
            ) { chunk ->
                sharedCollection(userId).whereIn("uuid", chunk)
            }

        combine(
            ownerQuery,
            sharedQuery
        ) { result ->
            Result.Success(result.toList().flatten())
        }
    } catch (e: Exception) {
        flowOf(e.asError(emptyList()))
    }
}

inline fun <reified T> chunkQuery(
    uuids: List<String>?,
    onNull: () -> Query = { throw NotImplementedError() },
    query: (List<String>) -> Query,
): Flow<List<T>> {
    if (uuids == null) return onNull().snapshots().map { it.toObjects(T::class.java) }
    return combine(
        uuids
            .chunked(10)
            .map(query)
            .map { it.snapshots().map { snap -> snap.toObjects(T::class.java) } }
    ) {
        it.toList().flatten()
    }
}
