package de.bitb.pantryplaner.usecase.location

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.LocationRepository
import de.bitb.pantryplaner.data.model.Location

class EditLocationUC(
    private val locationRepo: LocationRepository,
) {
    suspend operator fun invoke(
        location: Location,
        name: String = location.name,
        sharedWith: List<String> = location.sharedWith,
    ): Resource<Unit> {
        return tryIt(
            onTry = {
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