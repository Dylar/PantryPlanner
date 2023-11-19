package de.bitb.pantryplaner.test

import de.bitb.pantryplaner.core.createFlows
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.parsePOKO
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
    val recipesFlows =
        createFlows(recipes) { recipe -> (listOf(recipe.creator) + recipe.sharedWith) }

    coEvery { getRecipes(any(), any()) }.answers {
        val userId = firstArg<String>()
        val uuids = secondArg<List<String>?>()

        val flow = recipesFlows[userId] ?: MutableStateFlow(Result.Success(emptyList()))
        recipesFlows[userId] = flow

        allFlow.flatMapLatest { recipesList ->
            flow.apply {
                value = Result.Success(
                    recipesList
                        .filter { uuids?.contains(it.uuid) ?: true }
                        .filter { it.creator == userId || it.sharedWith.contains(userId) }
                )
            }
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

    coEvery { saveRecipe(any()) }.answers {
        val saveRecipe = firstArg<Recipe>()
        allFlow.value = allFlow.value
            .map { if (it.uuid == saveRecipe.uuid) saveRecipe else it }
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
        coEvery { saveRecipe(any()) }.answers { saveRecipeError }
}