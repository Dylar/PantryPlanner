package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.castOnError
import de.bitb.pantryplaner.core.misc.formatDateNow
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface ItemRepository {
    fun getItem(id: String): Flow<Result<Item>>
    fun getItems(ids: List<String>, filterBy: Filter? = null): Flow<Result<List<Item>>>
    fun getUserItems(
        ids: List<String>? = null,
        filterBy: Filter? = null
    ): Flow<Result<List<Item>>>

    suspend fun getAllItems(ids: List<String>?): Result<List<Item>>

    suspend fun addItem(item: Item): Result<Boolean>
    suspend fun deleteItem(item: Item): Result<Boolean>
    suspend fun saveItems(items: List<Item>): Result<Unit>
}

class ItemRepositoryImpl(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) : ItemRepository {
    override fun getItem(id: String): Flow<Result<Item>> {
        return remoteDB
            .getItems(localDB.getUser(), listOf(id))
            .map { resp ->
                castOnError(resp) {
                    Result.Success(resp.data?.first())
                }
            }
    }

    override fun getItems(
        ids: List<String>,
        filterBy: Filter?,
    ): Flow<Result<List<Item>>> {
        return remoteDB
            .getItems(ids)
            .map { resp -> // TODO make this "generic" (see below)
                castOnError(resp) {
                    val items = resp.data
                    items?.sortedBy { it.name }
                    items?.sortedBy { it.category } //TODO sort?
                    val groupedItems = items
                        ?.filter {
                            filterBy == null ||
                                    (!filterBy.filterByTerm && !filterBy.filterByColor) ||
                                    (filterBy.filterByTerm && it.name.lowercase()
                                        .contains(filterBy.searchTerm.lowercase()))
//                                    (filterBy.filterByColor && it.color == filterBy.color) // TODO ?
                        }
//                        ?.groupBy { it.category }
//                        ?.toSortedMap { a1, a2 -> a1.compareTo(a2) }
//                        ?: emptyMap()
                    Result.Success(groupedItems)
                }
            }
    }

    override fun getUserItems(
        ids: List<String>?,
        filterBy: Filter?,
    ): Flow<Result<List<Item>>> {
        return remoteDB
            .getItems(localDB.getUser(), ids)
            .map { resp -> // TODO make this "generic" (see above)
                castOnError(resp) {
                    val items = resp.data
                    items?.sortedBy { it.name }
                    items?.sortedBy { it.category } //TODO sort?
                    val groupedItems = items
                        ?.filter {
                            filterBy == null ||
                                    (!filterBy.filterByTerm && !filterBy.filterByColor) ||
                                    (filterBy.filterByTerm && it.name.lowercase()
                                        .contains(filterBy.searchTerm.lowercase()))
//                                    (filterBy.filterByColor && it.color == filterBy.color) // TODO ?
                        }
//                        ?.groupBy { it.category }
//                        ?.toSortedMap { a1, a2 -> a1.compareTo(a2) }
//                        ?: emptyMap()
                    Result.Success(groupedItems)
                }
            }
    }

    override suspend fun getAllItems(ids: List<String>?): Result<List<Item>> {
        return remoteDB
            .getItems(localDB.getUser(), ids)
            .map { resp ->
                castOnError(resp) {
                    val items = resp.data
                    items?.sortedBy { it.name } //TODO sort?
                    items?.sortedBy { it.category }
                    Result.Success(items)
                }
            }.first()
    }

    override suspend fun addItem(item: Item): Result<Boolean> {
        val now = formatDateNow()
        val user = localDB.getUser()
        return remoteDB.addItem(item.copy(creator = user, createdAt = now))
    }

    override suspend fun deleteItem(item: Item): Result<Boolean> =
        remoteDB.deleteItem(item)

    override suspend fun saveItems(items: List<Item>): Result<Unit> =
        remoteDB.saveItems(items)
}
