package de.bitb.pantryplaner.usecase.stock

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.model.StockItem
import kotlinx.coroutines.flow.first

class AddEditStockItemUC(
    private val userRepo: UserRepository,
    private val stockRepo: StockRepository,
) {

    suspend operator fun invoke(
        stock: Stock,
        stockItem: StockItem,
        amount: String = stockItem.amount.toString(),
//        freshUntil: Long = stockItem.freshUntil,
//        remindAfter: Long = stockItem.remindAfter,
    ): Resource<Unit> {
        return tryIt(
            onError = {
                when (it) {
                    is NumberFormatException -> {
                        if (amount.isEmpty()) {
                            Resource.Success()
                        } else "Not a number error".asResourceError()
                    }

                    else -> Resource.Error(it)
                }
            },
            onTry = {
                val user = userRepo.getUser().first()
                if (user is Resource.Error) return@tryIt user.castTo()
                val amountDouble = amount.replace(",", ".").toDouble()

                val updatedItem = stockItem.copy(
                    amount = amountDouble,
//                    freshUntil = freshUntil,
//                    remindAfter = remindAfter,
                )
                stock.items
                    .find { it.uuid == stockItem.uuid }
                    ?.let { stock.items[stock.items.indexOf(it)] = updatedItem }
                    ?: stock.items.add(updatedItem)

                stockRepo.saveStocks(listOf(stock))
            },
        )
    }
}