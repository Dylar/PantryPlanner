package de.bitb.pantryplaner.ui.checklist

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.castOnError
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.usecase.ChecklistUseCases
import de.bitb.pantryplaner.usecase.ItemUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val itemRepo: ItemRepository,
    private val checkRepo: CheckRepository,
    private val checkUseCases: ChecklistUseCases,
    private val itemUseCases: ItemUseCases,
) : BaseViewModel() {

    val itemErrorList = MutableStateFlow<List<String>>(emptyList())

    val filterBy = MutableStateFlow(Filter())

    lateinit var checkListId: String
    lateinit var checkList: Flow<Resource<Checklist>>
    lateinit var itemMap: Flow<Resource<Map<String, List<Item>>>>

    fun initChecklist(uuid: String) {
        checkListId = uuid
        checkList = checkRepo.getCheckList(checkListId)
        itemMap = checkList
            .flatMapLatest { resp ->
                val checklist = resp.data!!
                val ids = checklist.items.map { it.uuid }
                filterBy.flatMapLatest { filter ->
                    itemRepo.getItems(ids, filter)
                        .map { itemResp ->
                            castOnError(itemResp) {
                                // oh god
                                val newMap = mutableMapOf<String, List<Item>>()
                                itemResp.data?.forEach { lists ->
                                    newMap[lists.key] = lists.value.sortedBy { item ->
                                        checklist.items.first { it.uuid == item.uuid }.checked
                                    }
                                }
                                Resource.Success(newMap)
                            }
                        }
                }
            }
    }

    fun removeItem(item: Item) {
        viewModelScope.launch {
            val resp = checkUseCases.removeItemsFromChecklistUC(checkListId, listOf(item.uuid))
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

    fun editItem(item: Item) {
        viewModelScope.launch {
            when (val resp = itemUseCases.editItemUC(item)) {
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
                    navigateBack(null)
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
}
