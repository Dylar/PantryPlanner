package de.bitb.pantryplaner.test

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.parsePOKO
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.data.source.SettingsRemoteDao
import io.mockk.coEvery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

fun parseSettings(): Settings = parsePOKO("settings")

fun SettingsRemoteDao.mockSettingsDao(
    settings: Settings = Settings(),
    version: String = "0.0"
) {
    val flow = MutableStateFlow(Result.Success(settings))
    coEvery { getAppVersion() }.answers { Result.Success(version) }
    coEvery { getSettings(any()) }.answers { flow }
    coEvery { saveSettings(any()) }.answers {
        val saveSetting = firstArg<Settings>()
        flow.value = Result.Success(saveSetting)
        Result.Success()
    }
}

// TODO test errors
fun SettingsRemoteDao.mockErrorSettingsDao(
    getSettingsError: Result.Error<Settings>? = null,
    saveSettingsError: Result.Error<Unit>? = null,
) {
    if (getSettingsError != null)
        coEvery { getSettings(any()) }.answers { flowOf(getSettingsError) }
    if (saveSettingsError != null)
        coEvery { saveSettings(any()) }.answers { saveSettingsError }
}