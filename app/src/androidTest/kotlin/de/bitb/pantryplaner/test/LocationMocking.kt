package de.bitb.pantryplaner.test

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.formatDateNow
import de.bitb.pantryplaner.data.model.Location
import de.bitb.pantryplaner.data.source.LocationRemoteDao
import io.mockk.coEvery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

fun buildLocation(
    name: String = "Home",
    creator: String = "uuid1",
    createdAt: String = formatDateNow(),
    sharedWith: List<String> = listOf(),
): Location =
    Location(
        name = name,
        creator = creator,
        createdAt = createdAt,
        sharedWith = sharedWith,
    )

fun LocationRemoteDao.mockLocationDao(
    userLocations: MutableMap<String, List<Location>> = mutableMapOf()
) {
    val getLocationsFlows = userLocations
        .mapValues { MutableStateFlow<Resource<List<Location>>>(Resource.Success(it.value)) }
        .toMutableMap()

    coEvery { getLocations(any()) }.answers {
        val uuid = firstArg<String>()
        val flow = getLocationsFlows[uuid] ?: MutableStateFlow(Resource.Success(emptyList()))
        getLocationsFlows[uuid] = flow
        flow
    }
    coEvery { addLocation(any()) }.answers {
        val saveLocation = firstArg<Location>()
        val flow = getLocationsFlows[saveLocation.creator]
            ?: MutableStateFlow(Resource.Success(emptyList()))
        flow.value = Resource.Success(listOf(saveLocation, *flow.value.data!!.toTypedArray()))//
        Resource.Success(true)
    }
}

fun LocationRemoteDao.mockErrorLocationDao(
    getLocationsError: Resource.Error<List<Location>>? = null,
    addLocationError: Resource.Error<Boolean>? = null,
) {
    if (getLocationsError != null)
        coEvery { getLocations(any()) }.answers { flowOf(getLocationsError) }
    if (addLocationError != null)
        coEvery { addLocation(any()) }.answers { addLocationError }
}