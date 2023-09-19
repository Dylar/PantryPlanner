package de.bitb.pantryplaner.ui.items

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.UserDataExt
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.StockItem
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.usecase.ChecklistUseCases
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

data class StockModel(
    val stockItem: Map<String, StockItem>?,
    val items: Map<String, List<Item>>?,
    val connectedUser: List<User>?,
) {
    val isLoading: Boolean
        get() = stockItem == null || items == null || connectedUser == null
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class ItemsViewModel @Inject constructor(
    itemRepo: ItemRepository,
    stockRepo: StockRepository,
    override val userRepo: UserRepository,
    private val checkUseCases: ChecklistUseCases,
    private val itemUseCases: ItemUseCases,
    private val stockUseCases: StockUseCases,
) : BaseViewModel(), UserDataExt {
    private var fromChecklistId: String? = null

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    val filterBy = MutableStateFlow(Filter())
    val stockModel: LiveData<Resource<StockModel>> = filterBy
        .debounce { if (_isSearching.value) 1000L else 0L }
        .flatMapLatest { itemRepo.getItems(filterBy = it) }
        .flatMapLatest { itemsResp ->
            if (itemsResp is Resource.Error) return@flatMapLatest MutableStateFlow(itemsResp.castTo())
            val items = itemsResp.data!!.values.flatten().toSet()
            combine(
                stockRepo.getStockItems(items = items.map { it.uuid }.toList()),
                getConnectedUsers().asFlow(),
            ) { stockItems, users ->
                when {
                    stockItems is Resource.Error -> stockItems.castTo()
                    users is Resource.Error -> users.castTo()
                    else -> Resource.Success(
                        StockModel(
                            stockItems.data,
                            itemsResp.data,
                            users.data,
                        ),
                    )
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .asLiveData()

    val checkedItems = MutableStateFlow(listOf<String>())
    val itemErrorList = MutableStateFlow<List<String>>(emptyList())

    val isSelectMode: Boolean
        get() = fromChecklistId != null

    override fun isBackable(): Boolean = checkedItems.value.isEmpty()

    fun initItems(checkUuid: String?) {
        fromChecklistId = checkUuid
    }

    fun addItem(item: Item, stockItem: StockItem) {
        val name = item.name
        viewModelScope.launch {
            val addStockResp = stockUseCases.addStockItemUC(stockItem)
            val createItemResp = itemUseCases.createItemUC(item)
            when { //TODO guess we need to split item frag :D
                createItemResp is Resource.Error -> showSnackbar(createItemResp.message!!)
                addStockResp is Resource.Error -> showSnackbar(addStockResp.message!!)
                createItemResp.data == true -> showSnackbar("Item hinzugefügt: $name".asResString()).also { updateWidgets() }
                else -> showSnackbar("Item gibt es schon: $name".asResString())
            }
        }
    }

    fun deleteItem(item: Item, stockItem: StockItem) {
        viewModelScope.launch {
            val deleteItemResp = itemUseCases.deleteItemUC(item)
            val deleteStockResp = stockUseCases.deleteStockItemUC(stockItem)
            when { //TODO guess we need to split item frag :D
                deleteItemResp is Resource.Error -> showSnackbar(deleteItemResp.message!!)
                deleteStockResp is Resource.Error -> showSnackbar(deleteStockResp.message!!)
                deleteItemResp.data == true -> showSnackbar("Item entfernt: ${item.name}".asResString()).also { updateWidgets() }
                else -> showSnackbar("Item nicht entfernt: ${item.name}".asResString())
            }
        }
    }

    fun checkItem(uuid: String) {
        if (isSelectMode) {
            checkedItems.update {
                val items = it.toMutableList()
                if (!items.remove(uuid)) {
                    items.add(uuid)
                }
                items.toList()
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

    fun addToChecklist() {
        if (isSelectMode) {
            viewModelScope.launch {
                when (val resp =
                    checkUseCases.addItemsUC(fromChecklistId!!, checkedItems.value)) {
                    is Resource.Error -> showSnackbar(resp.message!!)
                    else -> navigateBack()
                }
            }
        }
    }

    fun changeItemAmount(itemId: String, amount: String) {
        val itemErrors = itemErrorList.value.toMutableList()
        itemErrors.remove(itemId)
        itemErrorList.value = itemErrors

        viewModelScope.launch {
            val resp = itemUseCases.editItemUC(itemId, amount)
            if (resp is Resource.Error) {
                showSnackbar(resp.message!!)
                val errors = itemErrorList.value.toMutableList()
                errors.add(itemId)
                itemErrorList.value = itemErrors
            }
        }
    }

    fun search(text: String) {
        _isSearching.value = true
        filterBy.value = filterBy.value.copy(searchTerm = text)
    }
}
