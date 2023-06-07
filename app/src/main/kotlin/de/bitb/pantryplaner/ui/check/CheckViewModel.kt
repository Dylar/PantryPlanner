package de.bitb.pantryplaner.ui.check

import androidx.lifecycle.LiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class CheckViewModel @Inject constructor(
    itemRepo: ItemRepository,
) : BaseViewModel() {

    val checkList: LiveData<List<Item>> = itemRepo.getLiveCheckList()

    fun checkItem(item: Item) {
        TODO("Not yet implemented")
    }

    fun addItem(name:String) {
        TODO("Not yet implemented")
    }
}
