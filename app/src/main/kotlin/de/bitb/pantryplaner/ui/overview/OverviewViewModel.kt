package de.bitb.pantryplaner.ui.overview

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.composable.asResString
import de.bitb.pantryplaner.usecase.ChecklistUseCases
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    checkRepo: CheckRepository,
    private val itemRepo: ItemRepository,
    private val checkUseCases: ChecklistUseCases,
) : BaseViewModel() {

    val checkList: Flow<Resource<List<Checklist>>> = checkRepo.getCheckLists()

    fun checklistItems(ids: List<String>): Flow<Resource<List<Item>>> = itemRepo.getLiveItems(ids)
  //TODO show item count
    fun addChecklist(name: String) {
        viewModelScope.launch {
            val resp = checkUseCases.addChecklistUC(name)
            when {
                resp is Resource.Error -> showSnackbar(resp.message!!)
                resp.data == true -> showSnackbar("Checklist hinzugefügt: $name".asResString())
                else -> showSnackbar("Checklist gibt es schon: $name".asResString())
            }
        }
    }

    fun removeChecklist(check: Checklist) {
        viewModelScope.launch {
            val resp = checkUseCases.removeChecklistUC(check)
            when {
                resp is Resource.Error -> showSnackbar(resp.message!!)
                resp.data == true -> showSnackbar("Checkliste entfernt: ${check.name}".asResString())
                else -> showSnackbar("Checkliste nicht entfernt: ${check.name}".asResString())
            }
        }
    }
}
