package de.bitb.pantryplaner.usecase.stock

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.model.StockItem

class DeleteStockItemUC(
    private val stockRepo: StockRepository,
) {
    suspend operator fun invoke(stockItem: StockItem): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = { stockRepo.deleteItem(stockItem) },
        )
    }
}