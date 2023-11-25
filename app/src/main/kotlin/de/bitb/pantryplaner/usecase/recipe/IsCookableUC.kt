package de.bitb.pantryplaner.usecase.recipe

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Recipe
import de.bitb.pantryplaner.data.model.Stock

class IsCookableUC {
    suspend operator fun invoke(
        stocks: List<Stock>,
        recipes: List<Recipe>,
    ): Result<Map<String, Boolean>> {
        return tryIt(
            onTry = {
                val cookableMap = recipes.associate { recipe ->
                    val cookable = stocks.any { stock ->
                        recipe.items.all { recipeItem ->
                            stock.items.any { stockItem ->
                                stockItem.uuid == recipeItem.uuid &&
                                        stockItem.amount >= recipeItem.amount
                            }
                        }
                    }
                    recipe.uuid to cookable
                }
                Result.Success(cookableMap)
            },
        )
    }
}