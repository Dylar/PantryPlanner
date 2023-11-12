package de.bitb.pantryplaner.ui.stock

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Result
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO just for now
var INSTANT_SEARCH = false

data class StocksModel(
    val settings: Settings? = null,
    val stocks: List<Stock>? = null,
    val items: Map<String, List<Item>>? = null,
    val user: User? = null,
    val connectedUser: List<User>? = null,
    val sharedUser: Map<String, List<User>>?,
) {
    val isLoading: Boolean
        get() = settings == null || stocks == null || items == null || connectedUser == null || user == null || sharedUser == null
}

@OptIn(FlowPreview::class)
@HiltViewModel
class StockViewModel @Inject constructor(
    itemRepo: ItemRepository,
    stockRepo: StockRepository,
    settingsRepo: SettingsRepository,
    override val userRepo: UserRepository,
    private val itemUseCases: ItemUseCases,
    private val stockUseCases: StockUseCases,
) : BaseViewModel(), UserDataExt {
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    val filterBy = MutableStateFlow(Filter())

    @Suppress("UNCHECKED_CAST")
    @OptIn(ExperimentalCoroutinesApi::class)
    val stocksModel: LiveData<Result<StocksModel>> =
        combine(
            filterBy.debounce { if (!INSTANT_SEARCH && _isSearching.value) 1000L else 0L },
            settingsRepo.getSettings(),
            stockRepo.getStocks(),
            getConnectedUsers().asFlow(),
            userRepo.getUser(),
        ) { filter, settings, stocks, connectedUser, user ->
            listOf(filter, settings, stocks, connectedUser, user)
        }.flatMapLatest { params ->
            // load everything
            val filter = params[0] as Filter
            val settingsResp = params[1] as Result<Settings>
            val stocksResp = params[2] as Result<List<Stock>>
            val connectedUserResp = params[3] as Result<List<User>>
            val userResp = params[4] as Result<User>
            when {
                settingsResp is Result.Error -> flowOf(settingsResp.castTo())
                stocksResp is Result.Error -> flowOf(stocksResp.castTo())
                connectedUserResp is Result.Error -> flowOf(connectedUserResp.castTo())
                userResp is Result.Error -> flowOf(userResp.castTo())
                else -> {
                    // assemble stock info
                    val stocks = stocksResp.data!!
                    val stockItems = stocks.associateBy({ it.uuid }, { it.items })
                    val stocksItemsIds =
                        stocks.asSequence()
                            .map { it.items }.flatten()
                            .filter { it.amount > 0.0 }
                            .map { it.uuid }.toList()
                    combine(
                        // load items
                        itemRepo.getItems(stocksItemsIds, filter),
                        itemRepo.getUserItems(filterBy = filter),
                        userRepo.getUser(stocks.flatMap { it.sharedWith }),
                    ) { stocksItems, userItemsResp, allUserResp ->
                        when {
                            stocksItems is Result.Error -> stocksItems.castTo()
                            userItemsResp is Result.Error -> userItemsResp.castTo()
                            allUserResp is Result.Error -> allUserResp.castTo()
                            else -> {
                                val allUser = allUserResp.data!!
                                val userItems = userItemsResp.data!!
                                val items = stockItems.mapValues { (_, value) ->
                                    (userItems + value // add all items to stockItems
                                        .asSequence()
                                        .filter { it.amount > 0.0 } // show unshared items only with amount
                                        .mapNotNull { stockItem -> stocksItems.data?.find { it.uuid == stockItem.uuid } })
                                        .distinctBy { it.uuid }
                                        .sortedBy { it.name }
                                }
                                val stockUserMap = stocks.groupBy { it.uuid }
                                    .mapValues { (_, stockList) ->
                                        val shared = stockList.first().sharedWith
                                        allUser.filter { shared.contains(it.uuid) }
                                    }
                                Result.Success(
                                    StocksModel(
                                        settingsResp.data,
                                        stocksResp.data,
                                        items,
                                        userResp.data,
                                        connectedUserResp.data,
                                        stockUserMap,
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
            .onEach { _isSearching.update { false } }
            .asLiveData(viewModelScope.coroutineContext)

    fun search(text: String) {
        _isSearching.value = true
        filterBy.value = filterBy.value.copy(searchTerm = text)
    }

    fun addStock(stock: Stock) {
        viewModelScope.launch {
            when (val resp = stockUseCases.addStockUC(stock)) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> showSnackBar("Lager hinzugefügt: ${stock.name}".asResString())
            }
        }
    }

    fun addItem(item: Item) {
        val name = item.name
        viewModelScope.launch {
            val createItemResp = itemUseCases.createItemUC(item)
            when {
                createItemResp is Result.Error -> showSnackBar(createItemResp.message!!)
                createItemResp.data == true -> showSnackBar("Item hinzugefügt: $name".asResString()).also { updateWidgets() }
                else -> showSnackBar("Item gibt es schon: $name".asResString())
            }
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            val deleteItemResp = itemUseCases.deleteItemUC(item)
            when {
                deleteItemResp is Result.Error -> showSnackBar(deleteItemResp.message!!)
                deleteItemResp.data == true -> showSnackBar("Item entfernt: ${item.name}".asResString()).also { updateWidgets() }
                else -> showSnackBar("Item nicht entfernt: ${item.name}".asResString())
            }
        }
    }

    fun editItem(item: Item) {
        viewModelScope.launch {
            when (val editItemResp = itemUseCases.editItemUC(item)) {
                is Result.Error -> showSnackBar(editItemResp.message!!)
                else -> showSnackBar("Item editiert".asResString()).also { updateWidgets() }
            }
        }
    }

    fun editCategory(
        previousCategory: String,
        newCategory: String,
        color: Color
    ) {
        viewModelScope.launch {
            when (val resp = itemUseCases.editCategoryUC(
                previousCategory,
                newCategory,
                color
            )) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> showSnackBar("Kategorie editiert".asResString()).also { updateWidgets() }
            }
        }
    }

    fun changeItemAmount(stock: Stock, item: StockItem, amount: String) {
        viewModelScope.launch {
            val editStockItemResp =
                stockUseCases.addEditStockItemUC(stock, item, amount = amount)
            if (editStockItemResp is Result.Error) {
                showSnackBar(editStockItemResp.message!!)
            }
        }
    }

    fun setSharedWith(stock: Stock, users: List<User>) {
        viewModelScope.launch {
            when (val resp = stockUseCases.editStockUC(
                stock,
                sharedWith = users.map { it.uuid })) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> updateWidgets()
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

}