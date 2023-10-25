package de.bitb.pantryplaner.test

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.parsePOKO
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.data.source.SettingsRemoteDao
import io.mockk.coEvery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

fun parseSettings(): Settings = parsePOKO("settings")

fun SettingsRemoteDao.mockSettingsDao(
    settings: Settings = Settings()
) {
    val flow = MutableStateFlow(Resource.Success(settings))
    coEvery { getSettings(any()) }.answers { flow }
    coEvery { saveSettings(any()) }.answers {
        val saveSetting = firstArg<Settings>()
        flow.value = Resource.Success(saveSetting)
        Resource.Success()
    }
}

// TODO test errors
fun SettingsRemoteDao.mockErrorSettingsDao(
    getSettingsError: Resource.Error<Settings>? = null,
    saveSettingsError: Resource.Error<Unit>? = null,
) {
    if (getSettingsError != null)
        coEvery { getSettings(any()) }.answers { flowOf(getSettingsError) }
    if (saveSettingsError != null)
        coEvery { saveSettings(any()) }.answers { saveSettingsError }
}