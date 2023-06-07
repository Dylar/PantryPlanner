package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getLiveCheckList(): Flow<Resource<List<Item>>>
}

class PantryRepositoryImpl(
    private val remoteDB: RemoteService,
) : ItemRepository {

    override fun getLiveCheckList(): Flow<Resource<List<Item>>> = remoteDB.getItems()
}
