package de.bitb.pantryplaner.usecase.location

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.LocationRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Location
import kotlinx.coroutines.flow.first

class DeleteLocationUC(
    private val userRepo: UserRepository,
    private val locationRepo: LocationRepository,
) {
    suspend operator fun invoke(location: Location): Resource<Boolean> {
        return tryIt(
            false,
            onTry = {
                val userResp = userRepo.getUser().first()
                if (userResp is Resource.Error) return@tryIt userResp.castTo(false)

                val user = userResp.data?.uuid
                if (user == location.creator) { //TODO what if other user using it?
                    locationRepo.deleteLocation(location)
                } else {
                    val newList = location.sharedWith.subtract(setOf(user!!))
                    locationRepo.saveLocations(listOf(location.copy(sharedWith = newList.toList())))
                    Resource.Success(true)
                }
            },
        )
    }
}