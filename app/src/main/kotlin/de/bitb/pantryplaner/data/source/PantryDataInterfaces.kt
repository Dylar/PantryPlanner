package de.bitb.pantryplaner.data.source

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Item
import kotlinx.coroutines.flow.Flow

// REMOTE
interface RemoteService : UserRemoteDao, ItemRemoteDao, CheckRemoteDao

class PantryRemoteService(itemService: FirestoreItemService, checkService: FirestoreCheckService) :
    RemoteService,
    UserRemoteDao by itemService,
    ItemRemoteDao by itemService,
    CheckRemoteDao by checkService

interface UserRemoteDao {
    suspend fun loginUser(): Resource<Boolean>
}

interface ItemRemoteDao {
    fun getItems(ids: List<String>?): Flow<Resource<List<Item>>>
    suspend fun addItem(item: Item): Resource<Boolean>
    suspend fun removeItem(item: Item): Resource<Boolean>
    suspend fun saveItems(items: List<Item>): Resource<Unit>
}

interface CheckRemoteDao {
    fun getCheckLists(): Flow<Resource<List<Checklist>>>
    suspend fun addChecklist(check: Checklist): Resource<Boolean>
    suspend fun removeChecklist(check: Checklist): Resource<Boolean>
    suspend fun saveChecklist(check: Checklist): Resource<Unit>
}
