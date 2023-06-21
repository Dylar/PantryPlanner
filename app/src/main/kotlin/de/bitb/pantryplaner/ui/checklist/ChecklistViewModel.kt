package de.bitb.pantryplaner.ui.checklist

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.composable.asResString
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.usecase.ItemUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val itemRepo: ItemRepository,
    private val checkRepo: CheckRepository,
    private val itemUseCases: ItemUseCases,
) : BaseViewModel() {

    val filterBy = MutableStateFlow(BaseColors.FilterColors.first())

    lateinit var checkListId: String
    lateinit var checkList: Flow<Resource<Checklist>>
    lateinit var itemMap: Flow<Resource<Map<String, List<Item>>>>

    @OptIn(ExperimentalCoroutinesApi::class)
    fun initChecklist(uuid: String) {
        checkListId = uuid
        checkList = checkRepo
            .getCheckLists(listOf(checkListId))
            .map {
                if (it is Resource.Error) {
                    return@map it.castTo<Checklist>()
                }
                Resource.Success(it.data!!.first())
            }
        itemMap = checkList
            .flatMapLatest {
                val ids = it.data?.items ?: emptyList()
                itemRepo.getItems(ids)
            }.combine(filterBy) { itemsResp, filter ->
                if (itemsResp is Resource.Error) {
                    return@combine itemsResp.castTo<Map<String, List<Item>>>()
                }
                val items =
                    if (filter != BaseColors.FilterColors.first())
                        itemsResp.data?.filter { it.color == filterBy.value }
                    else itemsResp.data
                val groupedItems =
                    items?.groupBy { it.category }
                        ?.toSortedMap { a1, a2 -> a1.compareTo(a2) }
                Resource.Success(groupedItems ?: emptyMap())
            }
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

    fun uncheckAllItems() {
        viewModelScope.launch {
            when (val resp = itemUseCases.uncheckAllItemsUC(filterBy.value)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> showSnackbar("Alle Haken entfernt".asResString()).also { updateWidgets() }
            }
        }
    }

}
