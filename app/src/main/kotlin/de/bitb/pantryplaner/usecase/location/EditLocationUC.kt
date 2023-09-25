package de.bitb.pantryplaner.usecase.location

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.LocationRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Location
import kotlinx.coroutines.flow.first

class EditLocationUC(
    private val userRepo: UserRepository,
    private val locationRepo: LocationRepository,
) {
    suspend operator fun invoke(
        location: Location,
        name: String = location.name,
        sharedWith: List<String> = location.sharedWith,
    ): Resource<Unit> {
        return tryIt(
            onTry = {
                val user = userRepo.getUser().first()
                if (user is Resource.Error) return@tryIt user.castTo()
                if (user.data!!.uuid != location.creator)
                    return@tryIt "Nur der Ersteller kann den Ort ändern".asResourceError()
                locationRepo.saveLocations(
                    listOf(
                        location.copy(
                            name = name,
                            sharedWith = sharedWith
                        )
                    )
                )
            },
        )
    }
}