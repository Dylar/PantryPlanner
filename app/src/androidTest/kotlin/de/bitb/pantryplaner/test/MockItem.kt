package de.bitb.pantryplaner.test

import de.bitb.pantryplaner.core.createFlows
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.parsePOKO
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.source.ItemRemoteDao
import io.mockk.coEvery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

fun parseItemCreator(): Item = parsePOKO("item_creator")
fun parseItemShared(): Item = parsePOKO("item_shared")
fun parseItemSelect(): Item = parsePOKO("item_select")

fun ItemRemoteDao.mockItemDao(
    items: MutableList<Item> = mutableListOf()
) {
    val stocksFlows = createFlows(items) { item ->
        (listOf(item.creator) + item.sharedWith)
    }

    coEvery { getItems(any(), any()) }.answers {
        val userId = firstArg<String>()
        val itemIds = secondArg<List<String>?>()
        val flow = stocksFlows[userId] ?: MutableStateFlow(Resource.Success(emptyList()))
        flow.value =
            Resource.Success(if (itemIds == null) items else items.filter { itemIds.contains(it.uuid) })
        stocksFlows[userId] = flow
        flow
    }
    coEvery { addItem(any()) }.answers {
        val addItem = firstArg<Item>()
        val userId = addItem.creator

        val flow = stocksFlows[userId]
            ?: MutableStateFlow(Resource.Success(emptyList()))
        flow.value = Resource.Success(listOf(addItem, *flow.value.data!!.toTypedArray()))
        stocksFlows[userId] = flow
        Resource.Success(true)
    }

    coEvery { deleteItem(any()) }.answers {
        val deleteStock = firstArg<Item>()
        val userId = deleteStock.creator

        val flow = stocksFlows[userId]
            ?: MutableStateFlow(Resource.Success(emptyList()))
        flow.value = Resource.Success(flow.value.data!!.subtract(setOf(deleteStock)).toList())
        stocksFlows[userId] = flow

        Resource.Success(true)
    }

    coEvery { saveItems(any()) }.answers {
        val saveItems = firstArg<List<Item>>().associateBy { it.uuid }
        stocksFlows.forEach { (userId, flow) ->
            val oldList = flow.value.data!!.toMutableList()
            val newList = items
                .apply { replaceAll { loc -> saveItems[loc.uuid] ?: loc } }
                .filter { item ->
                    oldList.firstOrNull { it.uuid === item.uuid } != null &&
                            (item.creator == userId || item.sharedWith.contains(userId))
                }
            flow.value = Resource.Success(newList)
        }

        Resource.Success()
    }
}

// TODO test errors
fun ItemRemoteDao.mockErrorItemDao(
    getItemsError: Resource.Error<List<Item>>? = null,
    addItemError: Resource.Error<Boolean>? = null,
    deleteItemError: Resource.Error<Boolean>? = null,
    saveItemError: Resource.Error<Unit>? = null,
) {
    if (getItemsError != null)
        coEvery { getItems(any(), any()) }.answers { flowOf(getItemsError) }
    if (addItemError != null)
        coEvery { addItem(any()) }.answers { addItemError }
    if (deleteItemError != null)
        coEvery { deleteItem(any()) }.answers { deleteItemError }
    if (saveItemError != null)
        coEvery { saveItems(any()) }.answers { saveItemError }
}