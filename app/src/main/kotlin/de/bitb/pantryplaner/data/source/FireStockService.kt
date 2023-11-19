package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.FirebaseFirestore
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FireStockService(
    private val firestore: FirebaseFirestore,
) : StockRemoteDao {

    private val collection = firestore
        .collection("stage")
        .document(BuildConfig.FLAVOR)
        .collection("stock")

    private fun ownerCollection(userId: String) =
        collection.whereEqualTo("creator", userId)

    private fun sharedCollection(userId: String) =
        collection.whereArrayContains("sharedWith", userId)

    override fun getStocks(
        userId: String,
    ): Flow<Result<List<Stock>>> {
        return getOwnedOrShared(
            userId,
            ownerCollection = ::ownerCollection,
            sharedCollection = ::sharedCollection,
        )
    }

    override suspend fun addStock(stock: Stock): Result<Boolean> {
        return tryIt {
            val querySnapshot = collection
                .whereEqualTo("uuid", stock.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                collection.add(stock).await()
                Result.Success(true)
            } else {
                Result.Success(false)
            }
        }
    }

    override suspend fun deleteStock(stock: Stock): Result<Boolean> {
        return tryIt {
            val querySnapshot = collection
                .whereEqualTo("uuid", stock.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                Result.Success(false)
            } else {
                querySnapshot.documents.first().reference.delete().await()
                Result.Success(true)
            }
        }
    }

    override suspend fun saveStocks(stocks: List<Stock>): Result<Unit> {
        return tryIt {
            firestore.batch().apply {
                stocks.chunked(10).forEach { chunk ->
                    collection
                        .whereIn("uuid", chunk.map { it.uuid })
                        .get().await().documents
                        .forEach { snap ->
                            val uuid = snap.data?.get("uuid") ?: ""
                            set(snap.reference, chunk.first { it.uuid == uuid })
                        }
                }
                commit()
            }
            Result.Success()
        }
    }
}