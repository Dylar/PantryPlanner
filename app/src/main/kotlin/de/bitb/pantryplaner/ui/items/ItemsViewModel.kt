package de.bitb.pantryplaner.ui.items

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.ItemRepository
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
class ItemsViewModel @Inject constructor(
    itemRepo: ItemRepository,
    private val itemUseCases: ItemUseCases,
) : BaseViewModel() {

    var showGridLayout = MutableStateFlow(true)
    var showFilterDialog = MutableStateFlow(false)
    val filterBy = MutableStateFlow(BaseColors.FilterColors.first())
    var showAddDialog = MutableStateFlow(false)
    var showAddToDialog = MutableStateFlow(false)

    val checkedItems = MutableStateFlow(listOf<String>())
    override fun isBackable(): Boolean = checkedItems.value.isEmpty()

    @OptIn(ExperimentalCoroutinesApi::class)
    val checkList: Flow<Resource<Map<String, List<Item>>>> = filterBy.flatMapLatest {
        itemRepo.getLiveItems().map { resp ->
            if (resp is Resource.Error) {
                return@map resp.castTo<Map<String, List<Item>>>()
            }

            val items = if (it != BaseColors.FilterColors.first())
                resp.data?.filter { it.color == filterBy.value }
            else resp.data
            val groupedItems =
                items?.groupBy { it.category }?.toSortedMap { a1, a2 -> a1.compareTo(a2) }
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

    fun checkItem(uuid: String) {
        checkedItems.update {
            it.toMutableList()
                .apply {
                    if (!remove(uuid)) {
                        add(uuid)
                    }
                }.toList()
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

    fun addToChecklist() {
        TODO("Not yet implemented")
    }

}
