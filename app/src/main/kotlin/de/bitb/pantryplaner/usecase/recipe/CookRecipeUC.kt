package de.bitb.pantryplaner.usecase.recipe

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.model.Recipe
import de.bitb.pantryplaner.data.model.Stock

class CookRecipeUC(
    private val stockRepo: StockRepository,
) {
    suspend operator fun invoke(recipe: Recipe, stock: Stock): Result<Boolean> {
        return tryIt(
            onTry = {
                val items = stock.items.toMutableList()
                for (recipeItem in recipe.items) {
                    val index = items.indexOfFirst { it.uuid == recipeItem.uuid }
                    if (index == -1) return@tryIt "Der Bestand hat die Items nicht".asError()
                    val stockItem = items[index]
                    if (stockItem.amount < recipeItem.amount) return@tryIt "Der Bestand hat nicht genug Items".asError()
                    items[index] = stockItem.copy(amount = stockItem.amount - recipeItem.amount)
                }

                val saveResp = stockRepo.saveStock(stock.copy(items = items))
                if (saveResp is Result.Error) return@tryIt saveResp.castTo(false)
                Result.Success(true)
            },
        )
    }
}