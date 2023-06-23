package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

interface ItemRepository {
    fun getItems(
        ids: List<String>? = null,
        filterBy: MutableStateFlow<Filter>
    ): Flow<Resource<Map<String, List<Item>>>>

    suspend fun addItem(item: Item): Resource<Boolean>
    suspend fun removeItem(item: Item): Resource<Boolean>
    suspend fun saveItems(items: List<Item>): Resource<Unit>
}

class ItemRepositoryImpl(
    private val remoteDB: RemoteService,
) : ItemRepository {

    override fun getItems(
        ids: List<String>?,
        filterBy: MutableStateFlow<Filter>
    ): Flow<Resource<Map<String, List<Item>>>> {
        return remoteDB
            .getItems(ids)
            .map { res ->
                if (res is Resource.Error) {
                    res
                } else {
                    tryIt {
                        val items = res.data
                        items?.sortedBy { it.name }
                        items?.sortedBy { it.category }
                        Resource.Success(items)
                    }
                }
            }.combine(filterBy) { resp, filter ->
                if (resp is Resource.Error) {
                    return@combine resp.castTo<Map<String, List<Item>>>()
                }

                val items =
                    if (filter.colorSelected) resp.data?.filter { it.color == filter.color }
                    else resp.data
                val groupedItems =
                    items?.groupBy { it.category }?.toSortedMap { a1, a2 -> a1.compareTo(a2) }
                Resource.Success(groupedItems ?: emptyMap())
            }
    }

    override suspend fun addItem(item: Item) = remoteDB.addItem(item)
    override suspend fun removeItem(item: Item): Resource<Boolean> = remoteDB.removeItem(item)
    override suspend fun saveItems(items: List<Item>): Resource<Unit> = remoteDB.saveItems(items)
}
