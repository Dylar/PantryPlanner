package de.bitb.pantryplaner.ui.items

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.usecase.ChecklistUseCases
import de.bitb.pantryplaner.usecase.ItemUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class ItemsViewModel @Inject constructor(
    itemRepo: ItemRepository,
    private val checkUseCases: ChecklistUseCases,
    private val itemUseCases: ItemUseCases,
) : BaseViewModel() {

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    var fromChecklistId: String? = null
    val filterBy = MutableStateFlow(Filter())
    val itemList: Flow<Resource<Map<String, List<Item>>>> = filterBy
        .debounce { if (_isSearching.value) 1000L else 0L }
        .flatMapLatest { itemRepo.getItems(filterBy = it) }
        .onEach { _isSearching.update { false } }

    val checkedItems = MutableStateFlow(listOf<String>())
    val itemErrorList = MutableStateFlow<List<String>>(emptyList())

    val isSelectMode: Boolean
        get() = fromChecklistId != null

    override fun isBackable(): Boolean = checkedItems.value.isEmpty()

    fun initItems(checkUuid: String?) {
        fromChecklistId = checkUuid
    }

    fun addItem(name: String, category: String, color: Color) {
        viewModelScope.launch {
            val resp = itemUseCases.addItemUC(name, category, color)
            when {
                resp is Resource.Error -> showSnackbar(resp.message!!)
                resp.data == true -> showSnackbar("Item hinzugefügt: $name".asResString()).also { updateWidgets() }
                else -> showSnackbar("Item gibt es schon: $name".asResString())
            }
        }
    }

    fun removeItem(item: Item) {
        viewModelScope.launch {
            val resp = itemUseCases.removeItemUC(item)
            when {
                resp is Resource.Error -> showSnackbar(resp.message!!)
                resp.data == true -> showSnackbar("Item entfernt: ${item.name}".asResString()).also { updateWidgets() }
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

    fun editItem(item: Item, name: String, category: String) {
        viewModelScope.launch {
            when (val resp = itemUseCases.editItemUC(item, name, category)) {
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
                    checkUseCases.addItemsToChecklistUC(fromChecklistId!!, checkedItems.value)) {
                    is Resource.Error -> showSnackbar(resp.message!!)
                    else -> navigateBack(null)
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
