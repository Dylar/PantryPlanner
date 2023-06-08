package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getLiveCheckList(): Flow<Resource<List<Item>>>
    suspend fun saveItem(item: Item): Resource<Boolean>
}

class ItemRepositoryImpl(
    private val remoteDB: RemoteService,
) : ItemRepository {

    override fun getLiveCheckList(): Flow<Resource<List<Item>>> = remoteDB.getItems()
    override suspend fun saveItem(item: Item): Resource<Boolean> = remoteDB.saveItem(item)
}
