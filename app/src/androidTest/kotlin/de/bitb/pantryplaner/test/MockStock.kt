package de.bitb.pantryplaner.test

import de.bitb.pantryplaner.core.createFlows
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.parsePOKO
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.source.StockRemoteDao
import io.mockk.coEvery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

fun parseStockCreator(): Stock = parsePOKO("stock_creator")
fun parseStockShared(): Stock = parsePOKO("stock_shared")

fun StockRemoteDao.mockStockDao(
    stocks: List<Stock> = emptyList()
) {
    val stocksFlows = createFlows(stocks) { stock ->
        (listOf(stock.creator) + stock.sharedWith)
    }

    coEvery { getStocks(any()) }.answers {
        val uuid = firstArg<String>()
        val flow = stocksFlows[uuid] ?: MutableStateFlow(Resource.Success(emptyList()))
        stocksFlows[uuid] = flow
        flow
    }
    coEvery { addStock(any()) }.answers {
        val addStock = firstArg<Stock>()
        val userId = addStock.creator

        val flow = stocksFlows[userId]
            ?: MutableStateFlow(Resource.Success(emptyList()))
        flow.value = Resource.Success(listOf(addStock, *flow.value.data!!.toTypedArray()))
        stocksFlows[userId] = flow
        Resource.Success(true)
    }

    coEvery { deleteStock(any()) }.answers {
        val deleteStock = firstArg<Stock>()
        val userId = deleteStock.creator

        val flow = stocksFlows[userId]
            ?: MutableStateFlow(Resource.Success(emptyList()))
        flow.value = Resource.Success(flow.value.data!!.subtract(setOf(deleteStock)).toList())
        stocksFlows[userId] = flow

        Resource.Success(true)
    }

    coEvery { saveStocks(any()) }.answers {
        val saveStocks = firstArg<List<Stock>>().associateBy { it.uuid }
        stocksFlows.forEach { (userId, flow) ->
            val newList = flow.value.data!!.toMutableList()
                .apply { replaceAll { stock -> saveStocks[stock.uuid] ?: stock } }
                .filter { it.creator == userId || it.sharedWith.contains(userId) }
            flow.value = Resource.Success(newList)
        }

        Resource.Success()
    }
}

// TODO test errors
fun StockRemoteDao.mockErrorStockDao(
    getStocksError: Resource.Error<List<Stock>>? = null,
    addStockError: Resource.Error<Boolean>? = null,
    deleteStockError: Resource.Error<Boolean>? = null,
    saveStockError: Resource.Error<Unit>? = null,
) {
    if (getStocksError != null)
        coEvery { getStocks(any()) }.answers { flowOf(getStocksError) }
    if (addStockError != null)
        coEvery { addStock(any()) }.answers { addStockError }
    if (deleteStockError != null)
        coEvery { deleteStock(any()) }.answers { deleteStockError }
    if (saveStockError != null)
        coEvery { saveStocks(any()) }.answers { saveStockError }
}