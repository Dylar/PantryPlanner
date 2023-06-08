package de.bitb.pantryplaner.ui.intro

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.atLeast
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.usecase.ItemUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val itemUseCases: ItemUseCases,
) : BaseViewModel() {

    fun loadData() {
        viewModelScope.launch {
            val userResp = atLeast(0) { itemUseCases.loadDataUC() }
            val route = if (userResp.data != true) {
                R.id.splash_to_check
            } else {
                R.id.splash_to_check // TODO error?
            }
            navigate(route)
        }
    }
}
