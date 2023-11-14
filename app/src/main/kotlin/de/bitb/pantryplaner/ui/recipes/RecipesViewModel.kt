package de.bitb.pantryplaner.ui.recipes

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.data.RecipeRepository
import de.bitb.pantryplaner.data.SettingsRepository
import de.bitb.pantryplaner.data.UserDataExt
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Recipe
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseViewModel
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class RecipesModel(
    val settings: Settings? = null,
    val recipes: List<Recipe>? = null,
    val user: User? = null,
) {
    val isLoading: Boolean
        get() = settings == null || recipes == null || user == null
}

@HiltViewModel
class RecipesViewModel @Inject constructor(
    recipeRepo: RecipeRepository,
    settingsRepo: SettingsRepository,
    override val userRepo: UserRepository,
//    private val recipeUseCases: RecipeUseCases,
) : BaseViewModel(), UserDataExt {
    val recipesModel: LiveData<Result<RecipesModel>> =
        combine(
            settingsRepo.getSettings(),
            recipeRepo.getRecipes(),
            userRepo.getUser(),
        ) { settings, recipes, user ->
            when {
                settings is Result.Error -> settings.castTo()
                recipes is Result.Error -> recipes.castTo()
                user is Result.Error -> user.castTo()
                else -> {
                    Result.Success(
                        RecipesModel(
                            settings.data,
                            recipes.data,
                            user.data,
                        ),
                    )
                }
            }
        }.asLiveData(viewModelScope.coroutineContext)

    fun addRecipe(stock: Recipe) {
//        viewModelScope.launch {
//            when (val resp = stockUseCases.addStockUC(stock)) {
//                is Result.Error -> showSnackBar(resp.message!!)
//                else -> showSnackBar("Lager hinzugefügt: ${stock.name}".asResString())
//            }
//        }
    }

    fun deleteRecipe(recipe: Recipe) {
//        viewModelScope.launch {
//            val deleteItemResp = itemUseCases.deleteItemUC(item)
//            when {
//                deleteItemResp is Result.Error -> showSnackBar(deleteItemResp.message!!)
//                deleteItemResp.data == true -> showSnackBar("Item entfernt: ${item.name}".asResString()).also { updateWidgets() }
//                else -> showSnackBar("Item nicht entfernt: ${item.name}".asResString())
//            }
//        }
    }

    fun editRecipe(recipe: Recipe) {
//        viewModelScope.launch {
//            when (val editItemResp = itemUseCases.editItemUC(item)) {
//                is Result.Error -> showSnackBar(editItemResp.message!!)
//                else -> showSnackBar("Item editiert".asResString()).also { updateWidgets() }
//            }
//        }
    }

    fun editCategory(
        previousCategory: String,
        newCategory: String,
        color: Color
    ) {
//        viewModelScope.launch {
//            when (val resp = itemUseCases.editCategoryUC(
//                previousCategory,
//                newCategory,
//                color
//            )) {
//                is Result.Error -> showSnackBar(resp.message!!)
//                else -> showSnackBar("Kategorie editiert".asResString()).also { updateWidgets() }
//            }
//        }
    }

    fun shareRecipe(recipe: Recipe) {
//        viewModelScope.launch {
//            when (val resp = itemUseCases.shareItemUC(item)) {
//                is Result.Error -> showSnackBar(resp.message!!)
//                else -> updateWidgets()
//            }
//        }
    }

}
