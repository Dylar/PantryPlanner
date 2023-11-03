package de.bitb.pantryplaner.usecase.user

import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Result
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
    suspend operator fun invoke(ignoreNewVersion: Boolean = false): Result<DataLoadResponse> {
        return tryIt {
            if (!ignoreNewVersion) {
                val appVersion = settingsRepo.getAppVersion()
                if (appVersion is Result.Error) return@tryIt appVersion.castTo()
                if (appVersion.data != BuildConfig.VERSION_NAME) {
                    return@tryIt Result.Success(DataLoadResponse.NewAppVersion)
                }
            }

            val resp = userRepo.isUserLoggedIn()
            if (resp is Result.Error) return@tryIt resp.castTo()

            Result.Success(
                if (resp.data == true) DataLoadResponse.DataLoaded
                else DataLoadResponse.NotLoggedIn
            )
        }
    }
}