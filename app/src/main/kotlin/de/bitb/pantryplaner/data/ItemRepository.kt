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

class ItemRepository(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) {
    fun getItem(id: String): Flow<Result<Item>> {
        return remoteDB.getItems(localDB.getUser(), listOf(id)).map { resp ->
            castOnError(resp) {
                Result.Success(resp.data?.first())
            }
        }
    }

    fun getItems(
        ids: List<String>,
        filterBy: Filter? = null,
    ): Flow<Result<List<Item>>> = remoteDB
        .getItems(ids)
        .map { resp -> filterList(resp, filterBy) }

    fun getUserItems(
        ids: List<String>? = null,
        filterBy: Filter? = null,
    ): Flow<Result<List<Item>>> = remoteDB
        .getItems(localDB.getUser(), ids)
        .map { resp -> filterList(resp, filterBy) }

    suspend fun getAllItems(ids: List<String>? = null): Result<List<Item>> {
        return remoteDB.getItems(localDB.getUser(), ids).map { resp ->
            castOnError(resp) {
                val items = resp.data
                items?.sortedBy { it.name } //TODO sort?
                items?.sortedBy { it.category }
                Result.Success(items)
            }
        }.first()
    }

    private suspend fun filterList(
        resp: Result<List<Item>>,
        filterBy: Filter?,
    ): Result<List<Item>> {
        return castOnError(resp) {
            val searchTerm = filterBy?.searchTerm?.lowercase() ?: ""
            Result.Success(
                resp.data
                    ?.filter {
                        filterBy == null ||
                                (!filterBy.filterByTerm && !filterBy.filterByColor) ||
                                (filterBy.filterByTerm && it.name.lowercase().contains(searchTerm))
                    }
                    ?.sortedBy { it.name },
            )
        }
    }

    suspend fun addItem(item: Item): Result<Boolean> {
        val now = formatDateNow()
        val user = localDB.getUser()
        return remoteDB.addItem(item.copy(creator = user, createdAt = now))
    }

    suspend fun deleteItem(item: Item): Result<Boolean> = remoteDB.deleteItem(item)

    suspend fun saveItems(items: List<Item>): Result<Unit> = remoteDB.saveItems(items)
}
