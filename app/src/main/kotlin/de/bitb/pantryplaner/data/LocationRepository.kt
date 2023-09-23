package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.castOnError
import de.bitb.pantryplaner.core.misc.formatDateNow
import de.bitb.pantryplaner.data.model.Location
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationRepository(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) {

    fun getLocations(
        userId: String? = null,
    ): Flow<Resource<List<Location>>> {
        return remoteDB.getLocations(userId ?: localDB.getUser())
    }

    suspend fun addLocation(location: Location): Resource<Boolean> {
        val now = formatDateNow()
        val user = localDB.getUser()
        return remoteDB.addLocation(location.copy(creator = user, createdAt = now))
    }

//    suspend fun addItem(item: StockItem): Resource<Boolean> =
//        remoteDB.addStockItem(localDB.getUser(), item.copy(updatedAt = formatDateNow()))
//
//    suspend fun deleteItem(item: StockItem): Resource<Boolean> =
//        remoteDB.deleteStockItem(localDB.getUser(), item)
//
//    suspend fun saveItems(items: List<StockItem>): Resource<Unit> {
//        return tryIt {
//            val stockResp = remoteDB.getStock(localDB.getUser()).first()
//            if (stockResp is Resource.Error) return@tryIt stockResp.castTo()
//            val updatedItems = items.map { it.copy(updatedAt = formatDateNow()) }
//            val stock = stockResp.data!!
//            val resultMap = stock.items.associateBy { it.uuid }.toMutableMap()
//            updatedItems.forEach { item -> resultMap[item.uuid] = item }
//            remoteDB.saveStock(
//                localDB.getUser(),
//                stock.copy(items = resultMap.values.toMutableList())
//            )
//        }
//    }
}
