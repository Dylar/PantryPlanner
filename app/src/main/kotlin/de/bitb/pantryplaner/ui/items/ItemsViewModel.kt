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
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.usecase.ChecklistUseCases
import de.bitb.pantryplaner.usecase.ItemUseCases
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ItemsViewModel @Inject constructor(
    itemRepo: ItemRepository,
    private val checkUseCases: ChecklistUseCases,
    private val itemUseCases: ItemUseCases,
) : BaseViewModel() {

    val itemErrorList = MutableStateFlow<List<String>>(emptyList())
    val checkedItems = MutableStateFlow(listOf<String>())

    var fromChecklistId: String? = null
    val filterBy = MutableStateFlow(Filter(BaseColors.UnselectedColor))
    val itemList: Flow<Resource<Map<String, List<Item>>>> = itemRepo.getItems(filterBy = filterBy)

    val isSelectModus: Boolean
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
        if (isSelectModus) {
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
        if (isSelectModus) {
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
}
