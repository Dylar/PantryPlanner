package de.bitb.pantryplaner.usecase.stock

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Stock
import kotlinx.coroutines.flow.first

class DeleteStockUC(
    private val userRepo: UserRepository,
    private val stockRepo: StockRepository,
) {
    suspend operator fun invoke(stock: Stock): Result<Boolean> {
        return tryIt(
            false,
            onTry = {
                val userResp = userRepo.getUser().first()
                if (userResp is Result.Error) return@tryIt userResp.castTo(false)

                val user = userResp.data?.uuid
                if (user == stock.creator) { //TODO what if other user using it? -> unlucky xD -> only if last otherwise change creator (creator = owner?)
                    stockRepo.deleteStock(stock)
                } else {
                    val newList = stock.sharedWith.subtract(setOf(user!!))
                    stockRepo.saveStock(stock.copy(sharedWith = newList.toList()))
                    Result.Success(true)
                }
            },
        )
    }
}