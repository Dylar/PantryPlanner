package de.bitb.pantryplaner.ui.stock

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.SettingsRepository
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.UserDataExt
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.model.StockItem
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.usecase.ItemUseCases
import de.bitb.pantryplaner.usecase.StockUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO just for now
var INSTANT_SEARCH = false

data class StockModel(
    val settings: Settings? = null,
    val stocks: List<Stock>? = null,
    val items: Map<String, List<Item>>? = null,
    val connectedUser: List<User>? = null,
    val user: User? = null,
) {
    val isLoading: Boolean
        get() = settings == null || stocks == null || items == null || connectedUser == null || user == null
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class StockViewModel @Inject constructor(
    itemRepo: ItemRepository,
    stockRepo: StockRepository,
    private val settingsRepo: SettingsRepository,
    override val userRepo: UserRepository,
    private val itemUseCases: ItemUseCases,
    private val stockUseCases: StockUseCases,
) : BaseViewModel(), UserDataExt {
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    val filterBy = MutableStateFlow(Filter())
    val stockModel: LiveData<Resource<StockModel>> = filterBy
        .debounce { if (!INSTANT_SEARCH && _isSearching.value) 1000L else 0L }
        .flatMapLatest {
            combine(
                settingsRepo.getSettings(),
                stockRepo.getStocks(),
                itemRepo.getItems(filterBy = it),
                getConnectedUsers().asFlow(),
                userRepo.getUser(),
            ) { settings, stocks, items, users, user ->
                when {
                    settings is Resource.Error -> settings.castTo()
                    stocks is Resource.Error -> stocks.castTo()
                    items is Resource.Error -> items.castTo()
                    users is Resource.Error -> users.castTo()
                    user is Resource.Error -> user.castTo()
                    else -> Resource.Success(
                        StockModel(
                            settings.data,
                            stocks.data,
                            items.data
                                ?.filter { item -> item.sharedWith(user.data!!.uuid) }
                                ?.groupBy { item -> item.category },
                            users.data,
                            user.data,
                        ),
                    )
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .asLiveData(viewModelScope.coroutineContext)

    fun addStock(stock: Stock) {
        viewModelScope.launch {
            when (val resp = stockUseCases.addStockUC(stock)) {
                is Resource.Error -> showSnackBar(resp.message!!)
                else -> showSnackBar("Lager hinzugefügt: ${stock.name}".asResString())
            }
        }
    }

    fun addItem(item: Item) {
        val name = item.name
        viewModelScope.launch {
            val createItemResp = itemUseCases.createItemUC(item)
            when {
                createItemResp is Resource.Error -> showSnackBar(createItemResp.message!!)
                createItemResp.data == true -> showSnackBar("Item hinzugefügt: $name".asResString()).also { updateWidgets() }
                else -> showSnackBar("Item gibt es schon: $name".asResString())
            }
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            val deleteItemResp = itemUseCases.deleteItemUC(item)
            when {
                deleteItemResp is Resource.Error -> showSnackBar(deleteItemResp.message!!)
                deleteItemResp.data == true -> showSnackBar("Item entfernt: ${item.name}".asResString()).also { updateWidgets() }
                else -> showSnackBar("Item nicht entfernt: ${item.name}".asResString())
            }
        }
    }

    fun editItem(item: Item) {
        viewModelScope.launch {
            when (val editItemResp = itemUseCases.editItemUC(item)) {
                is Resource.Error -> showSnackBar(editItemResp.message!!)
                else -> showSnackBar("Item editiert".asResString()).also { updateWidgets() }
            }
        }
    }

    fun editCategory(previousCategory: String, newCategory: String, color: Color) {
        viewModelScope.launch {
            when (val resp = itemUseCases.editCategoryUC(previousCategory, newCategory, color)) {
                is Resource.Error -> showSnackBar(resp.message!!)
                else -> showSnackBar("Kategorie editiert".asResString()).also { updateWidgets() }
            }
        }
    }

    fun changeItemAmount(stock: Stock, item: StockItem, amount: String) {
        viewModelScope.launch {
            val editStockItemResp = stockUseCases.addEditStockItemUC(stock, item, amount = amount)
            if (editStockItemResp is Resource.Error) {
                showSnackBar(editStockItemResp.message!!)
            }
        }
    }

    fun setSharedWith(stock: Stock, users: List<User>) {
        viewModelScope.launch {
            when (val resp = stockUseCases.editStockUC(stock, sharedWith = users.map { it.uuid })) {
                is Resource.Error -> showSnackBar(resp.message!!)
                else -> updateWidgets()
            }
        }
    }

    fun search(text: String) {
        _isSearching.value = true
        filterBy.value = filterBy.value.copy(searchTerm = text)
    }

}
