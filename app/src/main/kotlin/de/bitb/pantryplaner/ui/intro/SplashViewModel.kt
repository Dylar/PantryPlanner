package de.bitb.pantryplaner.ui.intro

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.atLeast
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.usecase.UserUseCases
import de.bitb.pantryplaner.usecase.item.DataLoadResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO just for now
var SPLASH_TIMER = 1000L

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val itemUseCases: UserUseCases,
) : BaseViewModel() {

    fun loadData(naviToRefresh: Boolean) {
        viewModelScope.launch {
            val userResp = atLeast(SPLASH_TIMER) { itemUseCases.loadDataUC() }
            when {
                userResp is Resource.Error -> showSnackbar(userResp.message!!)
                userResp.data is DataLoadResponse.DataLoaded -> {
                    navigate(R.id.splash_to_overview)
                    if (naviToRefresh) {
                        navigate(R.id.overview_to_refresh)
                    }
                }
                userResp.data is DataLoadResponse.NotLoggedIn -> {
                    navigate(R.id.splash_to_login)
                }
                else -> showSnackbar("Daten konnten nicht geladen werden".asResString())
            }
        }
    }
}
