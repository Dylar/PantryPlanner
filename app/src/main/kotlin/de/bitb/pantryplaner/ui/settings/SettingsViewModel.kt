package de.bitb.pantryplaner.ui.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.data.SettingsRepository
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository,
) : BaseViewModel() {

    val settings = settingsRepo.getSettings()

    fun saveSettings(settings: Settings) {
        viewModelScope.launch {
            settingsRepo.saveSettings(settings)
        }
    }

}

