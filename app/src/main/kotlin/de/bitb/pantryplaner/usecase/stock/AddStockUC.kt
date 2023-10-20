package de.bitb.pantryplaner.usecase.stock

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.model.Stock

class AddStockUC(
    private val stockRepo: StockRepository,
) {
    suspend operator fun invoke(Stock: Stock): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                val saveResp = stockRepo.addStock(Stock)
                if (saveResp is Resource.Error) {
                    return@tryIt saveResp.castTo(false)
                }
                Resource.Success(true)
            },
        )
    }
}