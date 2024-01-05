package de.bitb.pantryplaner.ui.recipes.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.RecipeRepository
import de.bitb.pantryplaner.data.SettingsRepository
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.UserDataExt
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.Recipe
import de.bitb.pantryplaner.data.model.RecipeItem
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.NaviEvent
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.usecase.ChecklistUseCases
import de.bitb.pantryplaner.usecase.ItemUseCases
import de.bitb.pantryplaner.usecase.RecipeUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class RecipeModel(
    val settings: Settings? = null,
    val recipe: Recipe? = null,
    val items: List<Item>? = null,
    val stocks: List<Stock>? = null,
    val checklists: List<Checklist>? = null,
    val user: User? = null,
    val connectedUser: List<User>? = null,
    val sharedUser: List<User>? = null,
    val isCookable: Boolean? = null,
) {
    val isLoading: Boolean
        get() = settings == null || stocks == null || checklists == null ||
                recipe == null || items == null || isCookable == null ||
                user == null || connectedUser == null || sharedUser == null

    fun isCreator(): Boolean = recipe?.isNew() == true || user?.uuid == recipe?.creator
    fun isSharedWith(item: Item): Boolean = item.sharedWith(user?.uuid ?: "")
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository,
    override val userRepo: UserRepository,
    private val stockRepo: StockRepository,
    private val checkRepo: CheckRepository,
    private val itemRepo: ItemRepository,
    private val recipeRepo: RecipeRepository,
    private val recipeUseCases: RecipeUseCases,
    private val itemUseCases: ItemUseCases,
    private val checkUseCases: ChecklistUseCases,
) : BaseViewModel(), UserDataExt {

    private var initialRecipe: Recipe? = null
    lateinit var recipeModel: LiveData<Result<RecipeModel>>
    private val recipeData: MutableStateFlow<Recipe?> = MutableStateFlow(null)

    var recipeName by mutableStateOf("")
    val recipeCategory = mutableStateOf(TextFieldValue())

    override fun isBackable(): Boolean =
        recipeData.value == initialRecipe &&
                recipeName == initialRecipe?.name &&
                recipeCategory.value.text == initialRecipe?.category

    fun initRecipe(uuid: String?) {
        recipeModel = initialLoad(uuid)
            .flatMapLatest(::recipeLoad)
            .asLiveData(viewModelScope.coroutineContext)
    }

    private fun initialLoad(uuid: String?): Flow<Result<RecipeModel>> {
        val isNew = uuid == null
        val recipeListId = uuid ?: UUID.randomUUID().toString()
        val recipeFlow = // load recipe from DB or create new
            if (isNew) flowOf(Result.Success(Recipe(recipeListId)))
            else recipeRepo.getRecipe(recipeListId)
        return flow { emit(recipeFlow.first()) } // emit just once
            .flatMapLatest { resp ->
                if (resp is Result.Error) return@flatMapLatest flowOf(resp.castTo())
                initialRecipe = resp.data // initial recipe data
                val cat = initialRecipe?.category?.ifBlank { "" }.orEmpty()
                recipeName = initialRecipe?.name?.ifBlank { "" }.orEmpty()
                recipeCategory.value = TextFieldValue(cat, TextRange(cat.length))
                recipeData.value = initialRecipe

                combine(
                    settingsRepo.getSettings(),
                    userRepo.getUser(),
                    getConnectedUsers().asFlow(),
                    stockRepo.getStocks(),
                    checkRepo.getCheckLists(),
                ) { settings, user, users, stocks, checklists ->
                    when {
                        settings is Result.Error -> settings.castTo()
                        user is Result.Error -> user.castTo()
                        users is Result.Error -> users.castTo()
                        stocks is Result.Error -> stocks.castTo()
                        checklists is Result.Error -> checklists.castTo()
                        else -> {
                            Result.Success(
                                RecipeModel(
                                    settings = settings.data,
                                    user = user.data,
                                    connectedUser = users.data,
                                    stocks = stocks.data,
                                    checklists = checklists.data?.filter { !it.finished }
                                )
                            )
                        }
                    }
                }
            }
            .flatMapLatest { resp -> // flatmap to recipe data
                if (resp is Result.Error) return@flatMapLatest flowOf(resp.castTo())
                recipeData.map { recipe ->
                    Result.Success(resp.data?.copy(recipe = recipe))
                }
            }
    }

    private fun recipeLoad(modelResp: Result<RecipeModel>): Flow<Result<RecipeModel>> {
        if (modelResp is Result.Error) return flowOf(modelResp.castTo())
        val model = modelResp.data
        val recipe = model?.recipe
        val itemIds = recipe?.items?.map { it.uuid }
        return combine(
            userRepo.getUser(recipe?.sharedWith.orEmpty()),
            itemRepo.getItems(itemIds.orEmpty()),
        ) { user, items ->
            when {
                user is Result.Error -> user.castTo()
                items is Result.Error -> items.castTo()
                else -> {
                    val cookableMap = recipeUseCases.isCookableUC(
                        model?.stocks.orEmpty(),
                        model?.recipe?.let { listOf(it) }.orEmpty()
                    )
                    if (cookableMap is Result.Error) return@combine cookableMap.castTo()
                    Result.Success(
                        model?.copy(
                            items = items.data,
                            sharedUser = user.data,
                            isCookable = cookableMap.data.orEmpty().values.first(),
                        )
                    )
                }
            }
        }
    }

    fun addItems(itemIds: List<String>) {
        viewModelScope.launch {
            recipeData.value?.let { recipe ->
                val items = recipe.items.toMutableList().apply {
                    addAll(itemIds.map { RecipeItem(it) })
                }
                recipeData.emit(recipe.copy(items = items))
            }
        }
    }

    fun shareItem(item: Item) {
        viewModelScope.launch {
            when (val resp = itemUseCases.shareItemUC(item)) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> updateWidgets()
            }
        }
    }

    fun removeItem(item: Item) {
        viewModelScope.launch {
            recipeData.value?.let { recipe ->
                val items = recipe.items.toMutableList().apply {
                    removeIf { it.uuid == item.uuid }
                }
                recipeData.emit(recipe.copy(items = items))
            }
        }
    }

    fun changeItemAmount(itemId: String, amount: String) {
        viewModelScope.launch {
            recipeData.value?.let { recipe ->
                val amountDouble = amount.replace(",", ".").toDouble()
                val item = recipe.items.first { it.uuid == itemId }
                item.amount = amountDouble
                recipeData.emit(recipe)
            }
        }
    }

    fun setName(text: String) {
        viewModelScope.launch {
            recipeData.value?.let { recipe ->
                recipeName = text
                recipeData.emit(recipe.copy(name = text))
            }
        }
    }

    fun setCategory(text: String) {
        viewModelScope.launch {
            recipeData.value?.let { recipe ->
                recipeCategory.value = TextFieldValue(text, TextRange(text.length))
                recipeData.emit(recipe.copy(category = text))
            }
        }
    }

    fun setSharedWith(users: List<User>) {
        viewModelScope.launch {
            recipeData.value?.let { recipe ->
                val sharedUser = recipe.sharedWith.toMutableList().apply {
                    clear()
                    addAll(users.map { it.uuid })
                }
                recipeData.emit(recipe.copy(sharedWith = sharedUser))
            }
        }
    }

    fun saveRecipe() {
        viewModelScope.launch {
            recipeData.value?.let { recipe ->
                val result =
                    if (recipe.isNew()) recipeUseCases.createRecipeUC(recipe)
                    else recipeUseCases.saveRecipeUC(recipe)
                when (result) {
                    is Result.Error -> showSnackBar(result.message!!)
                    else -> navigate(NaviEvent.NavigateBack)
                }
            }
        }
    }

    fun cookRecipe() {
        viewModelScope.launch {
            recipeModel.value?.data?.let { model ->
                val recipe = model.recipe!!
                val stock = model.stocks?.find { stock ->
                    recipe.items.all { recipeItem ->
                        stock.items.any { stockItem -> //TODO selectStockDialog
                            recipeItem.uuid == stockItem.uuid && stockItem.amount >= recipeItem.amount
                        }
                    }
                }
                if (stock == null) {
                    showSnackBar("Kein Bestand gefunden".asResString())
                } else {
                    when (val result = recipeUseCases.cookRecipeUC(recipe, stock)) {
                        is Result.Error -> showSnackBar(result.message!!)
                        else -> showSnackBar("Hoffentlich hat es geschmeckt <3".asResString())
                    }
                }
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
