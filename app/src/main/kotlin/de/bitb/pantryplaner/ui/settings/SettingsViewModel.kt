package de.bitb.pantryplaner.ui.settings

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.data.SettingsRepository
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.ui.intro.LoginFragment
import de.bitb.pantryplaner.usecase.UserUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository,
    private val userUC: UserUseCases,
) : BaseViewModel() {

    val settings = settingsRepo.getSettings().asLiveData(viewModelScope.coroutineContext)

    fun saveSettings(settings: Settings) {
        viewModelScope.launch {
            settingsRepo.saveSettings(settings)
        }
    }

    fun logout() {
        viewModelScope.launch {
            when (val resp = userUC.logoutUC()) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> {
                    showSnackBar("Benutzer abgemeldet".asResString())
                    navigate(LoginFragment.naviFromSettings)
                }
            }
        }
    }

}

