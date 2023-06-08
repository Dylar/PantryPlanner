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

    fun checkItem(item: Item) {

    }

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
}
