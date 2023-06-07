package de.bitb.pantryplaner.data.source

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Item
import kotlinx.coroutines.flow.Flow

// REMOTE
interface RemoteService : ItemRemoteDao

class PantryRemoteService(fireService: FirestoreService) :
    RemoteService,
    ItemRemoteDao by fireService

interface ItemRemoteDao {
     fun getItems(): Flow<Resource<List<Item>>>
    suspend fun removeItem(item: Item): Resource<Unit>
    suspend fun saveItem(item: Item): Resource<Unit>
}