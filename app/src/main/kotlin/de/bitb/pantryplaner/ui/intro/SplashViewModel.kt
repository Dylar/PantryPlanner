package de.bitb.pantryplaner.ui.intro

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.atLeast
import de.bitb.pantryplaner.data.SettingsRepository
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.NaviEvent
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.usecase.UserUseCases
import de.bitb.pantryplaner.usecase.user.DataLoadResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO just for now
var SPLASH_TIMER = 1000L

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository,
    private val itemUseCases: UserUseCases,
) : BaseViewModel() {

    val showNewAppDialog = mutableStateOf(false)

    fun loadData(naviToRefresh: Boolean, ignoreNewVersion: Boolean = false) {
        viewModelScope.launch {
            val userResp = atLeast(SPLASH_TIMER) { itemUseCases.loadDataUC(ignoreNewVersion) }
            when {
                userResp is Result.Error -> showSnackBar(userResp.message!!)
                userResp.data is DataLoadResponse.DataLoaded -> {
                    navigate(NaviEvent.Navigate(R.id.splash_to_checklists))
//                    if (naviToRefresh) { //TODO fix whole page
//                        navigate(NavigateEvent.Navigate(R.id.checklists_to_refresh))
//                    }
                }

                userResp.data is DataLoadResponse.NotLoggedIn -> {
                    navigate(NaviEvent.Navigate(R.id.splash_to_login))
                }

                userResp.data is DataLoadResponse.NewAppVersion -> {
                    showNewAppDialog.value = true
                }

                else -> showSnackBar("Daten konnten nicht geladen werden".asResString())
            }
        }
    }

    fun loadNewApp() {
        viewModelScope.launch {
            when (val url = settingsRepo.getAppDownloadURL()) {
                is Result.Success -> navigate(NaviEvent.NavigateToUrl(url.data!!))
                is Result.Error -> showSnackBar(url.message!!)
            }
        }
    }
}
