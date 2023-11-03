package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.formatDateNow
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow

class StockRepository(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) {

    fun getStocks(
        userId: String? = null,
    ): Flow<Result<List<Stock>>> {
        return remoteDB.getStocks(userId ?: localDB.getUser())
    }

    suspend fun addStock(stock: Stock): Result<Boolean> {
        val now = formatDateNow()
        val user = localDB.getUser()
        return remoteDB.addStock(stock.copy(creator = user, createdAt = now))
    }

    suspend fun deleteStock(stock: Stock): Result<Boolean> {
        return remoteDB.deleteStock(stock)
    }

    suspend fun saveStocks(stocks: List<Stock>): Result<Unit> {
        return remoteDB.saveStocks(stocks)
    }

}
