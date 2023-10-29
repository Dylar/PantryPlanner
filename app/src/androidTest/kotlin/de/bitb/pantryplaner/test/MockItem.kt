package de.bitb.pantryplaner.test

import de.bitb.pantryplaner.core.createFlows
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.parsePOKO
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.source.ItemRemoteDao
import io.mockk.coEvery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

fun parseItemCreator(): Item = parsePOKO("item_creator")
fun parseItemShared(): Item = parsePOKO("item_shared")
fun parseItemSelect(): Item = parsePOKO("item_select")
fun parseItemUnshared(): Item = parsePOKO("item_unshared")

@OptIn(ExperimentalCoroutinesApi::class)
fun ItemRemoteDao.mockItemDao(
    allItems: List<Item> = listOf()
) {
    val allFlow = MutableStateFlow(allItems)
    val itemsFlows = createFlows(allItems) { item ->
        (listOf(item.creator) + item.sharedWith)
    }

    coEvery { getItems(any()) }.answers {
        val itemIds = firstArg<List<String>>()
        allFlow.flatMapLatest { items ->
            MutableStateFlow(Resource.Success(items.filter { itemIds.contains(it.uuid) }))
        }
    }
    coEvery { getItems(any(), any()) }.answers {
        val userId = firstArg<String>()
        val itemIds = secondArg<List<String>?>()
        val flow = itemsFlows[userId] ?: MutableStateFlow(Resource.Success(emptyList()))
        itemsFlows[userId] = flow
        allFlow.flatMapLatest { items ->
            flow.apply {
                value = Resource.Success(
                    if (itemIds == null) items.filter { it.sharedWith(userId) }
                    else items.filter { itemIds.contains(it.uuid) && it.sharedWith(userId) }
                )
            }
        }
    }
    coEvery { addItem(any()) }.answers {
        val addItem = firstArg<Item>()
        allFlow.value = allFlow.value + listOf(addItem)
        Resource.Success(true)
    }

    coEvery { deleteItem(any()) }.answers {
        val deleteItem = firstArg<Item>()
        allFlow.value = allFlow.value - setOf(deleteItem)
        Resource.Success(true)
    }

    coEvery { saveItems(any()) }.answers {
        val saveItems = firstArg<List<Item>>().associateBy { it.uuid }
        allFlow.value = allFlow.value.map { saveItems[it.uuid] ?: it }
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
    if (getItemsError != null) {
        coEvery { getItems(any()) }.answers { flowOf(getItemsError) }
        coEvery { getItems(any(), any()) }.answers { flowOf(getItemsError) }
    }
    if (addItemError != null)
        coEvery { addItem(any()) }.answers { addItemError }
    if (deleteItemError != null)
        coEvery { deleteItem(any()) }.answers { deleteItemError }
    if (saveItemError != null)
        coEvery { saveItems(any()) }.answers { saveItemError }
}