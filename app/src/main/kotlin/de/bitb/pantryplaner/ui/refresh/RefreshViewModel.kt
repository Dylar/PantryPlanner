package de.bitb.pantryplaner.ui.refresh

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.formatDateString
import de.bitb.pantryplaner.core.misc.removeDuplicatesFromLists
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.UserDataExt
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.StockItem
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.usecase.ChecklistUseCases
import de.bitb.pantryplaner.usecase.ItemUseCases
import de.bitb.pantryplaner.usecase.StockUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RefreshModel(
    val stockItem: Map<String, StockItem>?,
    val items: Map<String, List<Item>>?,
) {
    val isLoading: Boolean
        get() = stockItem == null || items == null
}

@HiltViewModel
class RefreshViewModel @Inject constructor(
    itemRepo: ItemRepository,
    checkRepo: CheckRepository,
    stockRepo: StockRepository,
    override val userRepo: UserRepository,
    private val checkUseCases: ChecklistUseCases,
    private val itemUseCases: ItemUseCases,
    private val stockUseCases: StockUseCases,
) : BaseViewModel(), UserDataExt {

    val checkedItems = MutableStateFlow(listOf<String>())

    val refreshModel: LiveData<Resource<RefreshModel>> =
        combine(
//            itemRepo.getItems(), // just to update when amount changed (TODO do we need this after stock model?)
            checkRepo.getCheckLists(),
            stockRepo.getStocks(),
        ) { checkResp, stockResp ->
            if (checkResp is Resource.Error) return@combine checkResp.castTo()
            if (stockResp is Resource.Error) return@combine stockResp.castTo()

            val allLists = checkResp.data!!
            val unfinishedItems = allLists
                .asSequence()
                .filter { !it.finished }
                .map { it.items }
                .flatten()
                .toSet()
                .map { it.uuid }
            val stocks = stockResp.data!!
            val items = allLists
                .filter { it.finished }
                .map { check ->
                    val ids = check.items.map { it.uuid }
                    val itemResp = itemRepo.getAllItems(ids)
                    if (itemResp is Resource.Error) return@combine itemResp.castTo()

                    val finishDay = check.finishDate.toLocalDate()
                    formatDateString(finishDay) to itemResp.data!!.filter { item ->
                        val stockItem =
                            stocks.first().items.first { it.uuid == item.uuid } //TODO do for each stock? -> or with tabs on this page
                        !unfinishedItems.contains(item.uuid) && stockItem.isAlertable(finishDay)
                    }
                }
                .groupBy { it.first }
                .mapValues { (_, pairs) -> pairs.flatMap { it.second } }
                .toSortedMap(Comparator.reverseOrder())
                .removeDuplicatesFromLists()
                .filter { it.value.isNotEmpty() }

            Resource.Success(RefreshModel(stocks.first().items.associateBy { it.uuid }, items))
        }.asLiveData()

    fun clearItemAmount(itemId: String) {
//        viewModelScope.launch {
//            val resp = stockUseCases.editStockItemUC(itemId, "0") //TODO fix this page
//            if (resp is Resource.Error) showSnackbar(resp.message!!)
//        }
    }

    fun checkItem(uuid: String) {
        checkedItems.update {
            val items = it.toMutableList()
            if (!items.remove(uuid)) {
                items.add(uuid)
            }
            items.toList()
        }
    }

    fun addToNewChecklist(name: String, sharedWith: List<String>) {
        viewModelScope.launch {
            when (val resp =
                checkUseCases.createChecklistUC(name, checkedItems.value, sharedWith)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> navigateBack()
            }
        }
    }

}
