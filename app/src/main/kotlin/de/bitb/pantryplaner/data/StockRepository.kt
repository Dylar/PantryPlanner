package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.castOnError
import de.bitb.pantryplaner.core.misc.formatDateNow
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.StockItem
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class StockRepository(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) {

    fun getStockItems(
        userId: String? = null,
        items: List<String>? = null,
    ): Flow<Resource<Map<String, StockItem>>> {
        return remoteDB
            .getStock(userId ?: localDB.getUser())
            .map { resp ->
                castOnError(resp) {
                    val groupedItems = resp.data!!
                        .items
                        .filter { items?.contains(it.uuid) ?: true }
                        .groupBy { it.uuid }
                        .map { it.key to it.value.first() }
                        .toMap()
                    Resource.Success(groupedItems)
                }
            }
    }

    suspend fun addItem(item: StockItem): Resource<Boolean> =
        remoteDB.addStockItem(localDB.getUser(), item.copy(updatedAt = formatDateNow()))

    suspend fun deleteItem(item: StockItem): Resource<Boolean> =
        remoteDB.deleteStockItem(localDB.getUser(), item)

    suspend fun saveItems(items: List<StockItem>): Resource<Unit> {
        return tryIt {
            val stockResp = remoteDB.getStock(localDB.getUser()).first()
            if (stockResp is Resource.Error) return@tryIt stockResp.castTo()
            val updatedItems = items.map { it.copy(updatedAt = formatDateNow()) }
            val stock = stockResp.data!!
            val resultMap = stock.items.associateBy { it.uuid }.toMutableMap()
            updatedItems.forEach { item -> resultMap[item.uuid] = item }
            remoteDB.saveStock(localDB.getUser(),
                stock.copy(items = resultMap.values.toMutableList()))
        }
    }
}
