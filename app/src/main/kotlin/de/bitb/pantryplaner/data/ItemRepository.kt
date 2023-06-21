package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ItemRepository {
    fun getItems(ids: List<String>? = null): Flow<Resource<List<Item>>>
    suspend fun addItem(item: Item): Resource<Boolean>
    suspend fun removeItem(item: Item): Resource<Boolean>
    suspend fun saveItems(items: List<Item>): Resource<Unit>
}

class ItemRepositoryImpl(
    private val remoteDB: RemoteService,
) : ItemRepository {

    override fun getItems(ids: List<String>?): Flow<Resource<List<Item>>> =
        remoteDB.getItems(ids).map { res ->
            if (res is Resource.Error) {
                res
            } else {
                Resource.Success(res.data?.apply {
                    sortedBy { it.name }
                    sortedBy { it.category }
                    sortedBy { it.checked }
                })
            }
        }

    override suspend fun addItem(item: Item) = remoteDB.addItem(item)
    override suspend fun removeItem(item: Item): Resource<Boolean> = remoteDB.removeItem(item)
    override suspend fun saveItems(items: List<Item>): Resource<Unit> = remoteDB.saveItems(items)
}
