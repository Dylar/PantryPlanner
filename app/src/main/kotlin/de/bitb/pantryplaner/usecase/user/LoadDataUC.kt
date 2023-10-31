package de.bitb.pantryplaner.usecase.user

import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Logger
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.SettingsRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.ui.base.comps.ResString
import de.bitb.pantryplaner.ui.base.comps.asResString

sealed class DataLoadResponse(val message: ResString) {
    object NotLoggedIn : DataLoadResponse(R.string.not_logged_in.asResString())
    object DataLoaded : DataLoadResponse(R.string.data_loaded.asResString())
    object NewAppVersion : DataLoadResponse("Neue Version".asResString())
}

class LoadDataUC(
    private val settingsRepo: SettingsRepository,
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(ignoreNewVersion: Boolean = false): Resource<DataLoadResponse> {
        return tryIt {
            Logger.printLog("LOAD" to "DATA")
            if (!ignoreNewVersion) {
                Logger.printLog("LOAD" to "VERSION 1")
                val appVersion = settingsRepo.getAppVersion()
                if (appVersion is Resource.Error) {
                    Logger.printLog("LOAD" to "VERSION ERROR")
                    return@tryIt appVersion.castTo()
                }
                Logger.printLog(
                    "LOAD" to "VERSION 2",
                    "appV" to appVersion.data,
                    "buildV" to BuildConfig.VERSION_NAME
                )
                if (appVersion.data != BuildConfig.VERSION_NAME) {
                    return@tryIt Resource.Success(DataLoadResponse.NewAppVersion)
                }
            }
            Logger.printLog(
                "LOAD" to "MORE",
            )

            val resp = userRepo.isUserLoggedIn()
            if (resp is Resource.Error) return@tryIt resp.castTo()

            Resource.Success(
                if (resp.data == true) DataLoadResponse.DataLoaded
                else DataLoadResponse.NotLoggedIn
            )
        }
    }
}