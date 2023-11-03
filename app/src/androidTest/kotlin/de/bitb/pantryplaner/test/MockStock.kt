package de.bitb.pantryplaner.test

import de.bitb.pantryplaner.core.createFlows
import de.bitb.pantryplaner.core.misc.Result
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
        val flow = stocksFlows[uuid] ?: MutableStateFlow(Result.Success(emptyList()))
        stocksFlows[uuid] = flow
        flow
    }
    coEvery { addStock(any()) }.answers {
        val addStock = firstArg<Stock>()
        val userId = addStock.creator

        val flow = stocksFlows[userId]
            ?: MutableStateFlow(Result.Success(emptyList()))
        flow.value = Result.Success(listOf(addStock, *flow.value.data!!.toTypedArray()))
        stocksFlows[userId] = flow
        Result.Success(true)
    }

    coEvery { deleteStock(any()) }.answers {
        val deleteStock = firstArg<Stock>()
        val userId = deleteStock.creator

        val flow = stocksFlows[userId]
            ?: MutableStateFlow(Result.Success(emptyList()))
        flow.value = Result.Success(flow.value.data!!.subtract(setOf(deleteStock)).toList())
        stocksFlows[userId] = flow

        Result.Success(true)
    }

    coEvery { saveStocks(any()) }.answers {
        val saveStocks = firstArg<List<Stock>>().associateBy { it.uuid }
        stocksFlows.forEach { (userId, flow) ->
            val newList = flow.value.data!!.toMutableList()
                .apply { replaceAll { stock -> saveStocks[stock.uuid] ?: stock } }
                .filter { it.creator == userId || it.sharedWith.contains(userId) }
            flow.value = Result.Success(newList)
        }

        Result.Success()
    }
}

// TODO test errors
fun StockRemoteDao.mockErrorStockDao(
    getStocksError: Result.Error<List<Stock>>? = null,
    addStockError: Result.Error<Boolean>? = null,
    deleteStockError: Result.Error<Boolean>? = null,
    saveStockError: Result.Error<Unit>? = null,
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