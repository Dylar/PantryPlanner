package de.bitb.pantryplaner.ui.refresh

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.formatDateString
import de.bitb.pantryplaner.core.misc.removeDuplicatesFromLists
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.UserDataExt
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.usecase.ChecklistUseCases
import de.bitb.pantryplaner.usecase.ItemUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RefreshViewModel @Inject constructor(
    itemRepo: ItemRepository,
    checkRepo: CheckRepository,
    override val userRepo: UserRepository,
    private val checkUseCases: ChecklistUseCases,
    private val itemUseCases: ItemUseCases,
) : BaseViewModel(), UserDataExt {

    val checkedItems = MutableStateFlow(listOf<String>())

    @OptIn(ExperimentalCoroutinesApi::class)
    val itemList: Flow<Resource<Map<String, List<Item>>>> = itemRepo.getItems()
        .flatMapLatest { _ -> // just to update when amount changed
            checkRepo.getCheckLists()
                .mapLatest { resp ->
                    if (resp is Resource.Error) return@mapLatest resp.castTo(emptyMap())

                    val allLists = resp.data!!
                    val unfinishedItems = allLists
                        .asSequence()
                        .filter { !it.finished }
                        .map { it.items }
                        .flatten()
                        .toSet()
                        .map { it.uuid }
                    allLists
                        .filter { it.finished }
                        .map { check ->
                            val ids = check.items.map { it.uuid }
                            val itemResp = itemRepo.getAllItems(ids)
                            if (itemResp is Resource.Error) return@mapLatest itemResp.castTo(
                                emptyMap()
                            )

                            val finishDay = check.finishDate.toLocalDate()
                            formatDateString(finishDay) to itemResp.data!!
                                .filter {
                                    !unfinishedItems.contains(it.uuid) &&
                                            (!it.isFresh(finishDay) || it.remindIt(finishDay))
                                }
                        }
                        .groupBy { it.first }
                        .mapValues { (_, pairs) -> pairs.flatMap { it.second } }
                        .toSortedMap(Comparator.reverseOrder())
                        .removeDuplicatesFromLists()
                        .filter { it.value.isNotEmpty() }
                        .let { Resource.Success(it) }
                }
        }


    fun clearItemAmount(itemId: String) {
        viewModelScope.launch {
            val resp = itemUseCases.editItemUC(itemId, "0")
            if (resp is Resource.Error) showSnackbar(resp.message!!)
        }
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
                checkUseCases.addChecklistUC(name, checkedItems.value, sharedWith)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> navigateBack()
            }
        }
    }

}
