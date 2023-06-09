package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getLiveCheckList(): Flow<Resource<List<Item>>>
    suspend fun addItem(item: Item): Resource<Boolean>
    suspend fun removeItem(item: Item): Resource<Boolean>
    suspend fun saveItems(items: List<Item>): Resource<Unit>
}

class ItemRepositoryImpl(
    private val remoteDB: RemoteService,
) : ItemRepository {

    override fun getLiveCheckList(): Flow<Resource<List<Item>>> = remoteDB.getItems()
    override suspend fun addItem(item: Item)  = remoteDB.addItem(item)
    override suspend fun removeItem(item: Item): Resource<Boolean> = remoteDB.removeItem(item)
    override suspend fun saveItems(items: List<Item>): Resource<Unit> = remoteDB.saveItems(items)
}
