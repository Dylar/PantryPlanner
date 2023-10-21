package de.bitb.pantryplaner.ui.overview

import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.UserDataExt
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.usecase.ChecklistUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OverviewModel(
    val stocks: List<Stock>?,
    val connectedUser: List<User>?,
    val checkList: Map<Boolean, List<Checklist>>?,
) {
    val isLoading: Boolean
        get() = checkList == null || connectedUser == null || stocks == null
}

@HiltViewModel
class OverviewViewModel @Inject constructor(
    stockRepo: StockRepository,
    checkRepo: CheckRepository,
    override val userRepo: UserRepository,
    private val checkUseCases: ChecklistUseCases,
) : BaseViewModel(), UserDataExt {

    val overviewModel = combine(
        stockRepo.getStocks(),
        checkRepo.getCheckLists(),
        getConnectedUsers().asFlow(),
    ) { stocks, checklists, users ->
        when {
            stocks is Resource.Error -> stocks.castTo()
            checklists is Resource.Error -> checklists.castTo()
            users is Resource.Error -> users.castTo()
            else -> {
                val groupedChecklists = checklists.data
                    ?.groupBy { it.finished }
                    ?.toSortedMap { a1, a2 -> a1.compareTo(a2) }
                Resource.Success(
                    OverviewModel(
                        stocks.data!!,
                        users.data,
                        groupedChecklists,
                    )
                )
            }
        }
    }.asLiveData()

    fun addChecklist(check: Checklist) {
        viewModelScope.launch {
            val resp = checkUseCases.createChecklistUC(check)
            when {
                resp is Resource.Error -> showSnackbar(resp.message!!)
                resp.data == true -> showSnackbar("Checklist hinzugefügt: ${check.name}".asResString())
                else -> showSnackbar("Checklist gibt es schon: ${check.name}".asResString())
            }
        }
    }

    fun removeChecklist(check: Checklist) {
        viewModelScope.launch {
            val name = check.name
            val resp = checkUseCases.deleteChecklistUC(check)
            when {
                resp is Resource.Error -> showSnackbar(resp.message!!)
                resp.data == true -> showSnackbar("Checkliste entfernt: $name".asResString())
                else -> showSnackbar("Checkliste nicht entfernt: $name".asResString())
            }
        }
    }

    fun unfinishChecklist(check: Checklist) {
        viewModelScope.launch {
            when (val resp = checkUseCases.unfinishChecklistUC(check.uuid)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> showSnackbar("Checkliste geöffnet: ${check.name}".asResString())
            }
        }
    }

    fun editChecklist(check: Checklist) {
        viewModelScope.launch {
            when (val resp = checkUseCases.saveChecklistUC(check)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> showSnackbar("Checkliste editiert: ${check.name}".asResString())
            }
        }
    }

}
