package de.bitb.pantryplaner.usecase.item

import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.ui.base.comps.ResString
import de.bitb.pantryplaner.ui.base.comps.asResString

sealed class DataLoadResponse(val message: ResString) {
    object NotLoggedIn : DataLoadResponse(R.string.not_logged_in.asResString())
    object DataLoaded : DataLoadResponse(R.string.data_loaded.asResString())
}


class LoadDataUC(
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(): Resource<DataLoadResponse> {
        return tryIt {
            val resp = userRepo.isUserLoggedIn()
            if (resp is Resource.Error) return@tryIt resp.castTo()

            Resource.Success(
                if (resp.data == true) DataLoadResponse.DataLoaded
                else DataLoadResponse.NotLoggedIn
            )
        }
    }
}