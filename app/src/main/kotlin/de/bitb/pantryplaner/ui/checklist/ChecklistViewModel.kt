package de.bitb.pantryplaner.ui.checklist

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.composable.asResString
import de.bitb.pantryplaner.usecase.ItemUseCases
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChecklistViewModel @Inject constructor(
    itemRepo: ItemRepository,
    private val itemUseCases: ItemUseCases,
) : BaseViewModel() {

    val checkList: Flow<Resource<List<Item>>> = itemRepo.getLiveItems()

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

    fun checkItem(item: Item) {
        viewModelScope.launch {
            when (val resp = itemUseCases.checkItemUC(item)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> updateWidgets()
            }
        }
    }

    fun editItem(item: Item, name: String, category: String, color: Color) {
        viewModelScope.launch {
            when (val resp = itemUseCases.editItemUC(item, name, category, color)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> showSnackbar("Item editiert".asResString()).also { updateWidgets() }
            }
        }
    }

    fun editCategory(previousCategory: String, newCategory: String) {
        viewModelScope.launch {
            when (val resp = itemUseCases.editCategoryUC(previousCategory, newCategory)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> showSnackbar("Kategorie editiert".asResString()).also { updateWidgets() }
            }
        }
    }

    fun uncheckAllItems(color: Color) {
        viewModelScope.launch {
            when (val resp = itemUseCases.uncheckAllItemsUC(color)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> showSnackbar("Alle Haken entfernt".asResString()).also { updateWidgets() }
            }
        }
    }

}
