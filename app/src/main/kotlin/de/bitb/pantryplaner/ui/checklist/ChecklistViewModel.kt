package de.bitb.pantryplaner.ui.checklist

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.castOnError
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.SettingsRepository
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.UserDataExt
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.NavigateEvent
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.usecase.ChecklistUseCases
import de.bitb.pantryplaner.usecase.ItemUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckModel(
    val settings: Settings?,
    val checklist: Checklist?,
    val items: Map<String, List<Item>>?,
    val stocks: List<Stock>?,
    val user: User?,
    val connectedUser: List<User>?,
    val sharedUser: List<User>?,
) {
    val isLoading: Boolean
        get() = settings == null || checklist == null || items == null || stocks == null || user == null || connectedUser == null || sharedUser == null

    fun isCreator(): Boolean = user?.uuid == checklist?.creator
    fun isSharedWith(item: Item): Boolean = item.sharedWith(user?.uuid ?: "")
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository,
    override val userRepo: UserRepository,
    private val stockRepo: StockRepository,
    private val itemRepo: ItemRepository,
    private val checkRepo: CheckRepository,
    private val checkUseCases: ChecklistUseCases,
    private val itemUseCases: ItemUseCases,
) : BaseViewModel(), UserDataExt {

    val filterBy = MutableStateFlow(Filter())

    lateinit var checkModel: LiveData<Result<CheckModel>>

    lateinit var checkListId: String

    @Suppress("UNCHECKED_CAST")
    fun initChecklist(uuid: String) {
        checkListId = uuid
        checkModel = checkRepo.getCheckList(checkListId)
            .flatMapLatest { checkResp ->
                if (checkResp is Result.Error) return@flatMapLatest MutableStateFlow(checkResp.castTo())
                val checklist = checkResp.data!!
                val itemIds = checklist.items.map { it.uuid }
                val itemsFlow = filterBy.flatMapLatest { filter ->
                    itemRepo.getItems(itemIds, filter)
                        .map { itemResp ->
                            castOnError(itemResp) {
                                val newMap = itemResp.data
                                    ?.groupBy { it.category }
                                    ?.mapValues { (_, value) ->
                                        value.sortedBy { item ->
                                            checklist.items
                                                .find { it.uuid == item.uuid }?.checked ?: false
                                        }
                                    } ?: mutableMapOf()
                                Result.Success(newMap)
                            }
                        }
                }
                combine(
                    settingsRepo.getSettings(),
                    userRepo.getUser(),
                    getConnectedUsers().asFlow(),
                    userRepo.getUser(checklist.sharedWith),
                    itemsFlow,
                    stockRepo.getStocks(),
                ) { params ->
                    val settings = params[0] as Result<Settings>
                    val user = params[1] as Result<User>
                    val users = params[2] as Result<List<User>>
                    val sharedUsers = params[3] as Result<List<User>>
                    val items = params[4] as Result<Map<String, List<Item>>>
                    val stocks = params[5] as Result<List<Stock>>

                    when {
                        settings is Result.Error -> settings.castTo()
                        user is Result.Error -> user.castTo()
                        users is Result.Error -> users.castTo()
                        sharedUsers is Result.Error -> sharedUsers.castTo()
                        items is Result.Error -> items.castTo()
                        stocks is Result.Error -> stocks.castTo()
                        else -> {
                            //TODO just in test?
                            val filteredItems = //items.data
                                items.data?.mapValues { (_, list) ->
                                    list.filter { it.uuid in itemIds }
                                }?.filterValues { it.isNotEmpty() }

//                            items.data TODO maybe this?
//                                ?.filter { item -> item.sharedWith(user.data!!.uuid) }
//                                ?.groupBy { item -> item.category },
                            Result.Success(
                                CheckModel(
                                    settings.data,
                                    checklist,
                                    filteredItems,
                                    stocks.data,
                                    user.data,
                                    users.data,
                                    sharedUsers.data,
                                ),
                            )
                        }
                    }
                }
            }.asLiveData(viewModelScope.coroutineContext)
    }

    fun removeItem(item: Item) {
        viewModelScope.launch {
            val resp = checkUseCases.removeItemsUC(checkListId, listOf(item.uuid))
            when {
                resp is Result.Error -> showSnackBar(resp.message!!)
                resp.data == true -> showSnackBar("Item entfernt: ${item.name}".asResString()).also { updateWidgets() }
                else -> showSnackBar("Item nicht entfernt: ${item.name}".asResString())
            }
        }
    }

    fun checkItem(itemId: String) {
        //TODO track when items are checked (For sorting if item is at the beginning of the market or in the back)
        viewModelScope.launch {
            when (val resp = checkUseCases.checkItemUC(checkListId, itemId)) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> updateWidgets()
            }
        }
    }

    fun editCategory(previousCategory: String, newCategory: String, color: Color) {
        viewModelScope.launch {
            when (val resp = itemUseCases.editCategoryUC(previousCategory, newCategory, color)) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> showSnackBar("Kategorie editiert".asResString()).also { updateWidgets() }
            }
        }
    }

    fun finishChecklist() {
        viewModelScope.launch {
            when (val resp = checkUseCases.finishChecklistUC(checkListId)) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> {
                    showSnackBar("Erledigt".asResString())
                    navigate(NavigateEvent.NavigateBack)
                }
            }
        }
    }

    fun changeItemAmount(itemId: String, amount: String) {
        viewModelScope.launch {
            val resp = checkUseCases.setItemAmountUC(checkListId, itemId, amount)
            if (resp is Result.Error) {
                showSnackBar(resp.message!!)
            }
        }
    }

    fun setSharedWith(users: List<User>) {
        viewModelScope.launch {
            when (val resp = checkUseCases.setSharedWithUC(checkListId, users)) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> updateWidgets()
            }
        }
    }

    fun changeStock(stock: Stock) {
        viewModelScope.launch {
            when (val resp = checkUseCases.setStockWithUC(checkListId, stock.uuid)) {
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
