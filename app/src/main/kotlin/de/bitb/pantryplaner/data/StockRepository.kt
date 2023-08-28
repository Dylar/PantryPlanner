package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.castOnError
import de.bitb.pantryplaner.core.misc.formatDateNow
import de.bitb.pantryplaner.data.model.StockItem
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StockRepository(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) {

    fun getStockItems(userId: String? = null): Flow<Resource<Map<String, StockItem>>> {
        return remoteDB
            .getStock(userId ?: localDB.getUser())
            .map { resp ->
                castOnError(resp) {
                    val groupedItems = resp.data!!
                        .items
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

    suspend fun saveItems(items: List<StockItem>): Resource<Unit> =
        remoteDB.saveStockItems(localDB.getUser(),
            items.map { it.copy(updatedAt = formatDateNow()) })
}
