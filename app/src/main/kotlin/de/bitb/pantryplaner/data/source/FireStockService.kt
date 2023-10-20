package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.FirebaseFirestore
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Resource
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
    ): Flow<Resource<List<Stock>>> {
        //TODO what about on start without stock?
        return getOwnedOrShared(
            userId,
            ownerCollection = ::ownerCollection,
            sharedCollection = ::sharedCollection,
        )
    }

    override suspend fun addStock(stock: Stock): Resource<Boolean> {
        return tryIt {
            val querySnapshot = collection
                .whereEqualTo("uuid", stock.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                collection.add(stock).await()
                Resource.Success(true)
            } else {
                Resource.Success(false)
            }
        }
    }

    override suspend fun deleteStock(stock: Stock): Resource<Boolean> {
        return tryIt {
            val querySnapshot = collection
                .whereEqualTo("uuid", stock.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                Resource.Success(false)
            } else {
                querySnapshot.documents.first().reference.delete().await()
                Resource.Success(true)
            }
        }
    }

    override suspend fun saveStocks(stocks: List<Stock>): Resource<Unit> {
        return tryIt {
            firestore.batch().apply {
                collection
                    .whereIn("uuid", stocks.map { it.uuid })
                    .get().await().documents
                    .forEach { snap ->
                        val uuid = snap.data?.get("uuid") ?: ""
                        set(snap.reference, stocks.first { it.uuid == uuid })
                    }
                commit()
            }
            Resource.Success()
        }
    }

//    override suspend fun addStockItem(userId: String, item: StockItem): Resource<Boolean> {
//        return tryIt {
//            val querySnapshot = stockQuery(userId).get().await()
//            val stockExists = querySnapshot.isEmpty
//            val stock =
//                if (stockExists) Stock(items = mutableListOf(), sharedWith = listOf(userId))
//                else querySnapshot.first().toObject(Stock::class.java)
//
//            val contains = stock.items.firstOrNull { it.uuid == item.uuid } != null
//            if (!contains) {
//                stock.items.add(item)
//                if (stockExists) {
//                    stockCollection.add(stock)
//                } else {
//                    querySnapshot.documents.first().reference.set(stock)
//                }
//            }
//            Resource.Success(contains)
//        }
//    }
//
//    override suspend fun deleteStockItem(userId: String, item: StockItem): Resource<Boolean> {
//        return tryIt {
//            val querySnapshot = stockQuery(userId).get().await()
//            val stockExists = querySnapshot.isEmpty
//            val stock =
//                if (stockExists) Stock(items = mutableListOf(), sharedWith = listOf(userId))
//                else querySnapshot.first().toObject(Stock::class.java)
//
//            val stockItem = stock.items.firstOrNull { it.uuid == item.uuid }
//            val contains = stockItem != null
//            if (contains) {
//                stock.items.remove(stockItem)
//                if (stockExists) {
//                    stockCollection.add(stock)
//                } else {
//                    querySnapshot.documents.first().reference.set(stock)
//                }
//            }
//            Resource.Success(contains)
//        }
//    }

}