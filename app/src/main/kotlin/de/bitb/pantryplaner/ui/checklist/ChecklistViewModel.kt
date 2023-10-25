package de.bitb.pantryplaner.ui.checklist

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.castOnError
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.UserDataExt
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseViewModel
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
    val checklist: Checklist?,
    val items: Map<String, List<Item>>?,
    val stocks: List<Stock>?,
    val user: User?,
    val connectedUser: List<User>?,
    val sharedUser: List<User>?,
) {
    val isLoading: Boolean
        get() = checklist == null || items == null || stocks == null || user == null || connectedUser == null || sharedUser == null

    fun isCreator(): Boolean = user?.uuid == checklist?.creator
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChecklistViewModel @Inject constructor(
    override val userRepo: UserRepository,
    private val stockRepo: StockRepository,
    private val itemRepo: ItemRepository,
    private val checkRepo: CheckRepository,
    private val checkUseCases: ChecklistUseCases,
    private val itemUseCases: ItemUseCases,
) : BaseViewModel(), UserDataExt {

    val filterBy = MutableStateFlow(Filter())

    lateinit var checkModel: LiveData<Resource<CheckModel>>

    lateinit var checkListId: String

    fun initChecklist(uuid: String) {
        checkListId = uuid
        checkModel = checkRepo.getCheckList(checkListId)
            .flatMapLatest { checkResp ->
                if (checkResp is Resource.Error) return@flatMapLatest MutableStateFlow(checkResp.castTo())
                val checklist = checkResp.data!!
                val ids = checklist.items.map { it.uuid }
                val itemsFlow = filterBy.flatMapLatest { filter ->
                    itemRepo.getItems(ids, filter)
                        .map { itemResp ->
                            castOnError(itemResp) {
                                val newMap = itemResp.data?.groupBy { it.category }
                                    ?.mapValues { (_, value) ->
                                        value.sortedBy { item ->
                                            checklist.items
                                                .find { it.uuid == item.uuid }?.checked ?: false
                                        }
                                    } ?: mutableMapOf()
                                Resource.Success(newMap)
                            }
                        }
                }
                combine(
                    userRepo.getUser(),
                    getConnectedUsers().asFlow(),
                    userRepo.getUser(checklist.sharedWith),
                    itemsFlow,
                    stockRepo.getStocks()
                ) { user, users, sharedUsers, items, stocks ->
                    when {
                        user is Resource.Error -> user.castTo()
                        users is Resource.Error -> users.castTo()
                        sharedUsers is Resource.Error -> sharedUsers.castTo()
                        items is Resource.Error -> items.castTo()
                        stocks is Resource.Error -> stocks.castTo()
                        else -> {
                            //TODO just in test?
                            val filteredItems = items.data?.mapValues { (_, list) ->
                                list.filter { it.uuid in ids }
                            }?.filterValues { it.isNotEmpty() }

                            Resource.Success(
                                CheckModel(
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
            }.asLiveData()
    }

    fun removeItem(item: Item) {
        viewModelScope.launch {
            val resp = checkUseCases.removeItemsUC(checkListId, listOf(item.uuid))
            when {
                resp is Resource.Error -> showSnackbar(resp.message!!)
                resp.data == true -> showSnackbar("Item entfernt: ${item.name}".asResString()).also { updateWidgets() }
                else -> showSnackbar("Item nicht entfernt: ${item.name}".asResString())
            }
        }
    }

    fun checkItem(itemId: String) {
        viewModelScope.launch {
            when (val resp = checkUseCases.checkItemUC(checkListId, itemId)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> updateWidgets()
            }
        }
    }

    fun editCategory(previousCategory: String, newCategory: String, color: Color) {
        viewModelScope.launch {
            when (val resp = itemUseCases.editCategoryUC(previousCategory, newCategory, color)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> showSnackbar("Kategorie editiert".asResString()).also { updateWidgets() }
            }
        }
    }

    fun finishChecklist() {
        viewModelScope.launch {
            when (val resp = checkUseCases.finishChecklistUC(checkListId)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> {
                    showSnackbar("Erledigt".asResString())
                    navigateBack()
                }
            }
        }
    }

    fun changeItemAmount(itemId: String, amount: String) {
        viewModelScope.launch {
            val resp = checkUseCases.setItemAmountUC(checkListId, itemId, amount)
            if (resp is Resource.Error) {
                showSnackbar(resp.message!!)
            }
        }
    }

    fun setSharedWith(users: List<User>) {
        viewModelScope.launch {
            when (val resp = checkUseCases.setSharedWithUC(checkListId, users)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> updateWidgets()
            }
        }
    }

    fun changeStock(stock: Stock) {
        viewModelScope.launch {
            when (val resp = checkUseCases.setStockWithUC(checkListId, stock.uuid)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> updateWidgets()
            }
        }
    }
}
