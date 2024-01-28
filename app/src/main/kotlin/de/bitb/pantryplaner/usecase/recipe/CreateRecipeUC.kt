package de.bitb.pantryplaner.usecase.recipe

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.capitalizeFirstCharacter
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.RecipeRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Recipe
import kotlinx.coroutines.flow.first

class CreateRecipeUC(
    private val userRepo: UserRepository,
    private val recipeRepo: RecipeRepository
) {
    suspend operator fun invoke(
        recipe: Recipe
    ): Result<Boolean> {
        return tryIt(
            onError = { Result.Error(it, false) },
            onTry = {
                val name = recipe.name
                if (name.isBlank()) {
                    return@tryIt "Name darf nicht leer sein".asError()
                }
//                if (recipe.items.isEmpty()) {
//                    return@tryIt "Rezept muss mindestens ein Item haben".asError()
//                }

                val user = userRepo.getUser().first()
                if (user is Result.Error) return@tryIt user.castTo(false)

                val check = recipe.copy(
                    name = name.capitalizeFirstCharacter(),
                    creator = user.data!!.uuid,
                )
                recipeRepo.addRecipe(check)
            },
        )
    }
}