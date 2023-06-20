package de.bitb.pantryplaner.ui.intro

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.atLeast
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.composable.asResString
import de.bitb.pantryplaner.usecase.ItemUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val itemUseCases: ItemUseCases,
) : BaseViewModel() {

    fun loadData() {
        viewModelScope.launch {
            val userResp = atLeast(1000) { itemUseCases.loadDataUC() }
            when {
                userResp is Resource.Error -> showSnackbar(userResp.message!!)
                userResp.data == true -> navigate(R.id.splash_to_overview)
                else -> showSnackbar("Daten konnten nicht geladen werden".asResString())
            }
        }
    }
}
