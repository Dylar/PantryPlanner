package de.bitb.pantryplaner.data.source

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Item
import kotlinx.coroutines.flow.Flow

// REMOTE
interface RemoteService : ItemRemoteDao, UserRemoteDao

class PantryRemoteService(fireService: FirestoreService) :
    RemoteService,
    UserRemoteDao by fireService,
    ItemRemoteDao by fireService

interface UserRemoteDao {
    suspend fun loginUser(): Resource<Boolean>
}

interface ItemRemoteDao {
    fun getItems(): Flow<Resource<List<Item>>>
    suspend fun addItem(item: Item): Resource<Boolean>
    suspend fun removeItem(item: Item): Resource<Boolean>
    suspend fun saveItems(item: List<Item>): Resource<Unit>
}
