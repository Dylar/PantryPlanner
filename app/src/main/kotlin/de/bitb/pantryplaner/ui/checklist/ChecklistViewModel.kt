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
import de.bitb.pantryplaner.data.model.StockItem
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.usecase.ChecklistUseCases
import de.bitb.pantryplaner.usecase.ItemUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckModel(
    val isCreator: Boolean?,
    val checklist: Checklist?,
    val items: Map<String, List<Item>>?,
    val stocks: List<Stock>?, // TODO change to no stock needed -> on finish select stock (+ none, to not add? :D)
    val connectedUser: List<User>?,
    val sharedUser: List<User>?,
) {
    val isLoading: Boolean
        get() = isCreator == null || checklist == null || items == null || stocks == null || connectedUser == null || sharedUser == null
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

    val itemErrorList = MutableStateFlow<List<String>>(emptyList())
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
                                val newMap = itemResp.data?.mapValues { (_, value) ->
                                    value.sortedBy { item ->
                                        checklist.items.find { it.uuid == item.uuid }?.checked
                                            ?: false
                                    }
                                } ?: mutableMapOf()
                                Resource.Success(newMap)
                            }
                        }
                }
                val isCreatorFlow: Flow<Resource<Boolean>> = userRepo.getUser()
                    .flatMapLatest { userResp ->
                        if (userResp is Resource.Error) MutableStateFlow(userResp.castTo())
                        else Resource.Success(userResp.data!!.uuid == checklist.creator)
                            .asFlow<Boolean>()
                    }
                combine(
                    isCreatorFlow,
                    getConnectedUsers().asFlow(),
                    userRepo.getUser(checklist.sharedWith),
                    itemsFlow,
                    stockRepo.getStocks()
                ) { isCreator, users, sharedUsers, items, stocks ->
                    when {
                        isCreator is Resource.Error -> return@combine isCreator.castTo()
                        users is Resource.Error -> return@combine users.castTo()
                        sharedUsers is Resource.Error -> return@combine sharedUsers.castTo()
                        items is Resource.Error -> return@combine items.castTo()
                        stocks is Resource.Error -> return@combine stocks.castTo()
                        else -> {
                            //TODO just in test?
                            val filteredItems = items.data?.mapValues { (_, list) ->
                                list.filter { it.uuid in ids }
                            }?.filterValues { it.isNotEmpty() }
                            Resource.Success(
                                CheckModel(
                                    isCreator.data,
                                    checklist,
                                    filteredItems,
                                    stocks.data,
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

    fun editItem(stockItem: StockItem, item: Item) {
        viewModelScope.launch {
            when (val resp = itemUseCases.editItemUC(stockItem, item)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> showSnackbar("Item editiert".asResString()).also { updateWidgets() }
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
        val itemErrors = itemErrorList.value.toMutableList()
        itemErrors.remove(itemId)
        itemErrorList.value = itemErrors

        viewModelScope.launch {
            val resp = checkUseCases.setItemAmountUC(checkListId, itemId, amount)
            if (resp is Resource.Error) {
                showSnackbar(resp.message!!)
                val errors = itemErrorList.value.toMutableList()
                errors.add(itemId)
                itemErrorList.value = itemErrors
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
}
