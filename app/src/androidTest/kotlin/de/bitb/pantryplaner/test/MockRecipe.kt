package de.bitb.pantryplaner.test

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.parsePOKO
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.Recipe
import de.bitb.pantryplaner.data.source.RecipeRemoteDao
import io.mockk.coEvery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

fun parseRecipeCreator(): Recipe = parsePOKO("recipe_creator")
fun parseRecipeShared(): Recipe = parsePOKO("recipe_shared")

@OptIn(ExperimentalCoroutinesApi::class)
fun RecipeRemoteDao.mockRecipeDao(
    recipes: List<Recipe> = emptyList()
) {
    val allFlow = MutableStateFlow(recipes)
    coEvery { getRecipes(any(), any()) }.answers {
        val userId = firstArg<String>()
        val uuids = secondArg<List<String>?>()

        allFlow.flatMapLatest { recipesList ->
            val flow = MutableStateFlow<Result<List<Recipe>>>(Result.Success(emptyList()))
            flow.value = Result.Success(
                recipesList
                    .filter { uuids?.contains(it.uuid) ?: true }
                    .filter { it.creator == userId || it.sharedWith.contains(userId) }
            )
            flow
        }
    }
    coEvery { addRecipe(any()) }.answers {
        val addRecipe = firstArg<Recipe>()
        allFlow.value = allFlow.value + listOf(addRecipe)
        Result.Success(true)
    }

    coEvery { deleteRecipe(any()) }.answers {
        val deleteRecipe = firstArg<Recipe>()
        allFlow.value = allFlow.value - setOf(deleteRecipe)
        Result.Success(true)
    }

    coEvery { saveRecipes(any()) }.answers {
        val saveRecipes = firstArg<List<Recipe>>().associateBy { it.uuid }
        allFlow.value = allFlow.value.map { saveRecipes[it.uuid] ?: it }
        Result.Success()
    }

}

// TODO test errors
fun RecipeRemoteDao.mockErrorRecipeDao(
    getRecipesError: Result.Error<List<Recipe>>? = null,
    addRecipeError: Result.Error<Boolean>? = null,
    deleteRecipeError: Result.Error<Boolean>? = null,
    saveRecipeError: Result.Error<Unit>? = null,
) {
    if (getRecipesError != null)
        coEvery { getRecipes(any(), any()) }.answers { flowOf(getRecipesError) }
    if (addRecipeError != null)
        coEvery { addRecipe(any()) }.answers { addRecipeError }
    if (deleteRecipeError != null)
        coEvery { deleteRecipe(any()) }.answers { deleteRecipeError }
    if (saveRecipeError != null)
        coEvery { saveRecipes(any()) }.answers { saveRecipeError }
}