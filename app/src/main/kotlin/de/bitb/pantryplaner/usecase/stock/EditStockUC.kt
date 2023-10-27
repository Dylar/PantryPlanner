package de.bitb.pantryplaner.usecase.stock

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.capitalizeFirstCharacter
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Stock
import kotlinx.coroutines.flow.first

class EditStockUC(
    private val userRepo: UserRepository,
    private val stockRepo: StockRepository,
) {
    suspend operator fun invoke(
        stock: Stock,
        name: String = stock.name,
        sharedWith: List<String> = stock.sharedWith,
    ): Resource<Unit> {
        return tryIt(
            onTry = {
                val user = userRepo.getUser().first()
                if (user is Resource.Error) return@tryIt user.castTo()
                if (user.data!!.uuid != stock.creator)
                    return@tryIt "Nur der Ersteller kann das Lager ändern".asResourceError()
                stockRepo.saveStocks(
                    listOf(
                        stock.copy(
                            name = name.capitalizeFirstCharacter(),
                            sharedWith = sharedWith,
                        )
                    )
                )
            },
        )
    }
}