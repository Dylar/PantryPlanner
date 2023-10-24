package de.bitb.pantryplaner.ui.settings

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.SettingsRepository
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.usecase.UserUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository,
    private val userUC: UserUseCases,
) : BaseViewModel() {

    val settings = settingsRepo.getSettings().asLiveData()

    fun saveSettings(settings: Settings) {
        viewModelScope.launch {
            settingsRepo.saveSettings(settings)
        }
    }

    fun logout() {
        viewModelScope.launch {
            when (val resp = userUC.logoutUC()) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> {
                    showSnackbar("Benutzer abgemeldet".asResString())
                    navigate(R.id.settings_to_login)
                }
            }
        }
    }

}

