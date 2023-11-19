package de.bitb.pantryplaner.usecase.recipe

import de.bitb.pantryplaner.core.misc.Logger
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.RecipeRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Recipe
import kotlinx.coroutines.flow.first

class SaveRecipeUC(
    private val userRepo: UserRepository,
    private val recipeRepo: RecipeRepository
) {
    suspend operator fun invoke(recipe: Recipe): Result<Boolean> {
        return tryIt(
            onError = { Result.Error(it, false) },
            onTry = {
                val user = userRepo.getUser().first()
                if (user is Result.Error) return@tryIt user.castTo()
                if (user.data!!.uuid != recipe.creator)
                    return@tryIt "Nur der Ersteller kann das Rezept ändern".asError()

                val saveResp = recipeRepo.saveRecipe(recipe)
                if (saveResp is Result.Error) return@tryIt saveResp.castTo(false)
                else Result.Success(true)
            },
        )
    }
}