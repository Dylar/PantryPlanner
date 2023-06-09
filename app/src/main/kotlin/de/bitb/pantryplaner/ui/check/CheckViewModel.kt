package de.bitb.pantryplaner.ui.check

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
class CheckViewModel @Inject constructor(
    itemRepo: ItemRepository,
    private val itemUseCases: ItemUseCases,
) : BaseViewModel() {

    val checkList: Flow<Resource<List<Item>>> = itemRepo.getLiveCheckList()

    fun addItem(name: String) {
        viewModelScope.launch {
            val resp = itemUseCases.addItemUC(name)
            when {
                resp is Resource.Error -> showSnackbar(resp.message!!)
                resp.data == true -> showSnackbar("Item hinzugefügt".asResString())
                else -> showSnackbar("Item gibt es schon".asResString())
            }
        }
    }

    fun removeItem(item: Item) {
        viewModelScope.launch {
            val resp = itemUseCases.removeItemUC(item)
            when {
                resp is Resource.Error -> showSnackbar(resp.message!!)
                resp.data == true -> showSnackbar("Item entfernt".asResString())
                else -> showSnackbar("Item nicht entfernt".asResString())
            }
        }
    }

    fun checkItem(item: Item) {
        viewModelScope.launch {
            val resp = itemUseCases.checkItemUC(item)
            if (resp is Resource.Error) showSnackbar(resp.message!!)
        }
    }

    fun uncheckAllItems() {
        viewModelScope.launch {
            val resp = itemUseCases.uncheckAllItemsUC()
            when (resp) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> showSnackbar("Alle Haken entfernt".asResString())
            }
        }
    }
}
