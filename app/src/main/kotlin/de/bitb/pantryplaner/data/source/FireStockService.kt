package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.model.StockItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FireStockService(
    private val firestore: FirebaseFirestore,
) : StockItemRemoteDao {

    private val stockCollection = firestore
        .collection("stage")
        .document(BuildConfig.FLAVOR)
        .collection("stock")

    private fun stockQuery(userId: String) = stockCollection
        .whereArrayContains("sharedWith", userId)

    override fun getStock(
        userId: String,
    ): Flow<Resource<Stock>> {
        return stockQuery(userId)
            .snapshots()
            .map {
                val stock = it.toObjects(Stock::class.java).firstOrNull()
                    ?: Stock(sharedWith = listOf(userId))
                Resource.Success(stock)
            }
    }

    override suspend fun saveStock(userId: String, stock: Stock): Resource<Unit> {
        return tryIt {
            val query = stockQuery(userId).get().await()
            if (query.isEmpty) stockCollection.add(stock)
            else query.documents.first().reference.set(stock)
            Resource.Success()
        }
    }

    override suspend fun addStockItem(userId: String, item: StockItem): Resource<Boolean> {
        return tryIt {
            val querySnapshot = stockQuery(userId).get().await()
            val stockExists = querySnapshot.isEmpty
            val stock =
                if (stockExists) Stock(items = mutableListOf(), sharedWith = listOf(userId))
                else querySnapshot.first().toObject(Stock::class.java)

            val contains = stock.items.firstOrNull { it.uuid == item.uuid } != null
            if (!contains) {
                stock.items.add(item)
                if (stockExists) {
                    stockCollection.add(stock)
                } else {
                    querySnapshot.documents.first().reference.set(stock)
                }
            }
            Resource.Success(contains)
        }
    }

    override suspend fun deleteStockItem(userId: String, item: StockItem): Resource<Boolean> {
        return tryIt { // TODO look at me
            val querySnapshot = stockQuery(userId).get().await()
            val stockExists = querySnapshot.isEmpty
            val stock =
                if (stockExists) Stock(items = mutableListOf(), sharedWith = listOf(userId))
                else querySnapshot.first().toObject(Stock::class.java)

            val stockItem = stock.items.firstOrNull { it.uuid == item.uuid }
            val contains = stockItem != null
            if (contains) {
                stock.items.remove(stockItem)
                if (stockExists) {
                    stockCollection.add(stock)
                } else {
                    querySnapshot.documents.first().reference.set(stock)
                }
            }
            Resource.Success(contains)
        }
    }

}