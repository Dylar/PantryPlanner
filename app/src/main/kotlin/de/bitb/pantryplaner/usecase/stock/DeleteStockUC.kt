package de.bitb.pantryplaner.usecase.stock

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Stock
import kotlinx.coroutines.flow.first

class DeleteStockUC(
    private val userRepo: UserRepository,
    private val stockRepo: StockRepository,
) {
    suspend operator fun invoke(stock: Stock): Resource<Boolean> {
        return tryIt(
            false,
            onTry = {
                val userResp = userRepo.getUser().first()
                if (userResp is Resource.Error) return@tryIt userResp.castTo(false)

                val user = userResp.data?.uuid
                if (user == stock.creator) { //TODO what if other user using it?
                    stockRepo.deleteStock(stock)
                } else {
                    val newList = stock.sharedWith.subtract(setOf(user!!))
                    stockRepo.saveStocks(listOf(stock.copy(sharedWith = newList.toList())))
                    Resource.Success(true)
                }
            },
        )
    }
}