package de.bitb.pantryplaner.usecase.recipe

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.RecipeRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Recipe
import kotlinx.coroutines.flow.first

class DeleteRecipeUC(
    private val userRepo: UserRepository,
    private val recipeRepo: RecipeRepository,
) {
    suspend operator fun invoke(recipe: Recipe): Result<Boolean> {
        return tryIt(
            onError = { Result.Error(it, false) },
            onTry = {
                val userResp = userRepo.getUser().first()
                if (userResp is Result.Error) return@tryIt userResp.castTo(false)

                val user = userResp.data?.uuid
                if (user == recipe.creator) { //TODO what if other user using it? -> unlucky xD -> only if last otherwise change creator (creator = owner?)
                    recipeRepo.deleteRecipe(recipe)
                } else {
                    val newList = recipe.sharedWith.subtract(setOf(user!!))
                    recipeRepo.saveRecipe(recipe.copy(sharedWith = newList.toMutableList()))
                    Result.Success(true)
                }
            },
        )
    }
}