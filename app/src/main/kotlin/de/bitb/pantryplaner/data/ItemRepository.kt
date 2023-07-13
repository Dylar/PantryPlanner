package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.castOnError
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface ItemRepository {
    fun getItem(id: String): Flow<Resource<Item>>
    fun getItems(
        ids: List<String>? = null,
        filterBy: Filter? = null,
    ): Flow<Resource<Map<String, List<Item>>>>

    suspend fun getAllItems(ids: List<String>?): Resource<List<Item>>

    suspend fun addItem(item: Item): Resource<Boolean>
    suspend fun removeItem(item: Item): Resource<Boolean>
    suspend fun saveItems(items: List<Item>): Resource<Unit>
}

class ItemRepositoryImpl(
    private val remoteDB: RemoteService,
) : ItemRepository {
    override fun getItem(id: String): Flow<Resource<Item>> {
        return remoteDB
            .getItems(listOf(id))
            .map { resp ->
                castOnError(resp) {
                    Resource.Success(resp.data?.first())
                }
            }
    }

    override fun getItems(
        ids: List<String>?,
        filterBy: Filter?
    ): Flow<Resource<Map<String, List<Item>>>> {
        return remoteDB
            .getItems(ids)
            .map { resp ->
                castOnError(resp) {
                    val items = resp.data
                    items?.sortedBy { it.name }
                    items?.sortedBy { it.category }
                    val groupedItems = items
                        ?.filter {
                            filterBy == null ||
                                    (!filterBy.filterByTerm && !filterBy.filterByColor) ||
                                    (filterBy.filterByTerm && it.name.contains(filterBy.searchTerm)) ||
                                    (filterBy.filterByColor && it.color == filterBy.color)
                        }
                        ?.groupBy { it.category }
                        ?.toSortedMap { a1, a2 -> a1.compareTo(a2) }
                        ?: emptyMap()
                    Resource.Success(groupedItems)
                }
            }
    }

    override suspend fun getAllItems(ids: List<String>?): Resource<List<Item>> {
        return remoteDB
            .getItems(ids)
            .map { resp ->
                castOnError(resp) {
                    val items = resp.data
                    items?.sortedBy { it.name }
                    items?.sortedBy { it.category }
                    Resource.Success(items)
                }
            }.first()
    }

    override suspend fun addItem(item: Item) = remoteDB.addItem(item)
    override suspend fun removeItem(item: Item): Resource<Boolean> = remoteDB.removeItem(item)
    override suspend fun saveItems(items: List<Item>): Resource<Unit> = remoteDB.saveItems(items)
}
