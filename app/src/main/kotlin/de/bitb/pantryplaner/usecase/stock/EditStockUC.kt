package de.bitb.pantryplaner.usecase.stock

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Stock
import kotlinx.coroutines.flow.first

class EditStockUC(
    private val userRepo: UserRepository,
    private val StockRepo: StockRepository,
) {
    suspend operator fun invoke(
        Stock: Stock,
        name: String = Stock.name,
        sharedWith: List<String> = Stock.sharedWith,
    ): Resource<Unit> {
        return tryIt(
            onTry = {
                val user = userRepo.getUser().first()
                if (user is Resource.Error) return@tryIt user.castTo()
                if (user.data!!.uuid != Stock.creator)
                    return@tryIt "Nur der Ersteller kann den Ort ändern".asResourceError()
                StockRepo.saveStocks(
                    listOf(
                        Stock.copy(
                            name = name,
                            sharedWith = sharedWith
                        )
                    )
                )
            },
        )
    }
}