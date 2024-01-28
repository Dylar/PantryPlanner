package de.bitb.pantryplaner.ui.recipes

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.RecipeRepository
import de.bitb.pantryplaner.data.SettingsRepository
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.UserDataExt
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Recipe
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.usecase.ChecklistUseCases
import de.bitb.pantryplaner.usecase.RecipeUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipesModel(
    val settings: Settings? = null,
    val checklists: List<Checklist>? = null,
    val recipes: List<Recipe>? = null,
    val cookableMap: Map<String, Boolean>? = null,
) {
    val isLoading: Boolean
        get() = settings == null || checklists == null || recipes == null || cookableMap == null
}

@HiltViewModel
class RecipesViewModel @Inject constructor(
    settingsRepo: SettingsRepository,
    checkRepo: CheckRepository,
    stockRepo: StockRepository,
    recipeRepo: RecipeRepository,
    override val userRepo: UserRepository,
    private val checkUseCases: ChecklistUseCases,
    private val recipeUseCases: RecipeUseCases,
) : BaseViewModel(), UserDataExt {

    val recipesModel: LiveData<Result<RecipesModel>> =
        combine(
            settingsRepo.getSettings(),
            checkRepo.getCheckLists(),
            stockRepo.getStocks(),
            recipeRepo.getRecipes(),
        ) { settingsResp, checkResp, stocksResp, recipesResp ->
            when {
                settingsResp is Result.Error -> settingsResp.castTo()
                checkResp is Result.Error -> checkResp.castTo()
                stocksResp is Result.Error -> stocksResp.castTo()
                recipesResp is Result.Error -> recipesResp.castTo()
                else -> {
                    val stocks = stocksResp.data.orEmpty()
                    val recipes = recipesResp.data.orEmpty()
                    val cookableMap = recipeUseCases.isCookableUC(stocks, recipes)
                    Result.Success(
                        RecipesModel(
                            settingsResp.data,
                            checkResp.data?.filter { !it.finished },
                            recipesResp.data,
                            cookableMap.data,
                        ),
                    )
                }
            }
        }.asLiveData(viewModelScope.coroutineContext)

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val deleteRecipeResp = recipeUseCases.deleteRecipeUC(recipe)
            when {
                deleteRecipeResp is Result.Error -> showSnackBar(deleteRecipeResp.message!!)
                deleteRecipeResp.data == true -> showSnackBar("Item entfernt: ${recipe.name}".asResString()).also { updateWidgets() }
                else -> showSnackBar("Item nicht entfernt: ${recipe.name}".asResString())
            }
        }
    }

    fun editCategory(
        previousCategory: String,
        newCategory: String,
        color: Color
    ) {
        viewModelScope.launch {
            when (val resp = recipeUseCases.editCategoryUC(
                previousCategory,
                newCategory,
                color
            )) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> showSnackBar("Kategorie editiert".asResString()).also { updateWidgets() }
            }
        }
    }

    fun addRecipeToChecklist(check: Checklist, recipe: Recipe) {
        viewModelScope.launch {
            when (val resp = checkUseCases.addRecipeUC(check, recipe)) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> showSnackBar("Zutaten wurden \"${check.name}\" hinzugefügt".asResString())
            }
        }
    }

}
