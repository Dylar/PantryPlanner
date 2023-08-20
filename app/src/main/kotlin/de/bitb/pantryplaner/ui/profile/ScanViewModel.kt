package de.bitb.pantryplaner.ui.profile

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.usecase.UserUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val useCases: UserUseCases,
) : BaseViewModel() {

    fun onScan(scanText: String) {
        viewModelScope.launch {
            val res = useCases.connectUserUC(scanText)
            if (res is Resource.Error) showSnackbar(res.message!!)
            else {
                showSnackbar("Benutzer hinzugefügt".asResString())
                navigateBack()
            }
        }
    }
}

