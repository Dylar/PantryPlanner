package de.bitb.pantryplaner.usecase.location

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.LocationRepository
import de.bitb.pantryplaner.data.model.Location

class AddLocationUC(
    private val locationRepo: LocationRepository,
) {
    suspend operator fun invoke(location: Location): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                val saveResp = locationRepo.addLocation(location)
                if (saveResp is Resource.Error) {
                    return@tryIt saveResp.castTo(false)
                }
                Resource.Success(true)
            },
        )
    }
}