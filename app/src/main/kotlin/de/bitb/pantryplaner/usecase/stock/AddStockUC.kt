package de.bitb.pantryplaner.usecase.stock

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.capitalizeFirstCharacter
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.model.Stock

class AddStockUC(
    private val stockRepo: StockRepository,
) {
    suspend operator fun invoke(stock: Stock): Result<Boolean> {
        return tryIt(
            onError = { Result.Error(it, false) },
            onTry = {
                val saveResp =
                    stockRepo.addStock(stock.copy(name = stock.name.capitalizeFirstCharacter()))
                if (saveResp is Result.Error) return@tryIt saveResp.castTo(false)
                Result.Success(true)
            },
        )
    }
}