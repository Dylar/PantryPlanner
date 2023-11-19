package de.bitb.pantryplaner.ui.select

import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.SettingsRepository
import de.bitb.pantryplaner.data.UserDataExt
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.data.model.groupByCategory
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.NaviEvent
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.ui.stock.INSTANT_SEARCH
import de.bitb.pantryplaner.usecase.ChecklistUseCases
import de.bitb.pantryplaner.usecase.ItemUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SelectItemsModel(
    val settings: Settings?,
    val items: Map<String, List<Item>>?,
    val connectedUser: List<User>?,
) {
    val isLoading: Boolean
        get() = settings == null || items == null || connectedUser == null
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SelectItemsViewModel @Inject constructor(
    itemRepo: ItemRepository,
    private val settingsRepo: SettingsRepository,
    override val userRepo: UserRepository,
    private val checkUseCases: ChecklistUseCases,
    private val itemUseCases: ItemUseCases,
) : BaseViewModel(), UserDataExt {
    private var fromChecklistId: String? = null // TODO from Recipe?

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    val filterBy = MutableStateFlow(Filter())
    val itemsModel: LiveData<Result<SelectItemsModel>> = filterBy
        .debounce { if (!INSTANT_SEARCH && _isSearching.value) 1000L else 0L }
        .flatMapLatest { filter ->
            combine(
                settingsRepo.getSettings(),
                itemRepo.getUserItems(filterBy = filter),
                getConnectedUsers().asFlow(),
            ) { settings, items, users ->
                when {
                    settings is Result.Error -> settings.castTo()
                    items is Result.Error -> items.castTo()
                    users is Result.Error -> users.castTo()
                    else -> Result.Success(
                        SelectItemsModel(
                            settings.data,
                            items.data?.groupByCategory,
                            users.data,
                        ),
                    )
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .asLiveData(viewModelScope.coroutineContext)

    val checkedItems = MutableStateFlow(listOf<String>())

    override fun isBackable(): Boolean = checkedItems.value.isEmpty()

    fun initItems(checkUuid: String?) {
        fromChecklistId = checkUuid
    }

    fun search(text: String) {
        _isSearching.value = true
        filterBy.value = filterBy.value.copy(searchTerm = text)
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

    fun addItem(item: Item) {
        val name = item.name
        viewModelScope.launch {
            val createItemResp = itemUseCases.createItemUC(item)
            when {
                createItemResp is Result.Error -> showSnackBar(createItemResp.message!!)
                createItemResp.data == true -> showSnackBar("Item hinzugefügt: $name".asResString()).also { updateWidgets() }
                else -> showSnackBar("Item gibt es schon: $name".asResString())
            }
        }
    }

    fun addToChecklist() {
        viewModelScope.launch {
            when (val resp =
                checkUseCases.addItemsUC(fromChecklistId!!, checkedItems.value)) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> navigate(NaviEvent.NavigateBack)
            }
        }
    }
}
