package de.bitb.pantryplaner.test

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.formatDateNow
import de.bitb.pantryplaner.data.model.Location
import de.bitb.pantryplaner.data.source.LocationRemoteDao
import io.mockk.coEvery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

fun buildLocation(
    name: String = "DefaultLocation",
    creator: String = defaultUuid,
    createdAt: String = formatDateNow(),
    sharedWith: List<String> = listOf(),
): Location =
    Location(
        name = name,
        creator = creator,
        createdAt = createdAt,
        sharedWith = sharedWith,
    )

fun LocationRemoteDao.mockDefaultLocationDao() {
    val loc = buildLocation(creator = defaultUuid)
    mockLocationDao(mutableMapOf(defaultUuid to listOf(loc)))
}

fun LocationRemoteDao.mockLocationDao(
    userLocations: MutableMap<String, List<Location>> = mutableMapOf()
) {
    val locationsFlows = userLocations
        .mapValues { MutableStateFlow<Resource<List<Location>>>(Resource.Success(it.value)) }
        .toMutableMap()

    coEvery { getLocations(any()) }.answers {
        val uuid = firstArg<String>()
        val flow = locationsFlows[uuid] ?: MutableStateFlow(Resource.Success(emptyList()))
        locationsFlows[uuid] = flow
        flow
    }
    coEvery { addLocation(any()) }.answers {
        val addLocation = firstArg<Location>()
        val flow = locationsFlows[addLocation.creator]
            ?: MutableStateFlow(Resource.Success(emptyList()))
        flow.value = Resource.Success(listOf(addLocation, *flow.value.data!!.toTypedArray()))
        locationsFlows[addLocation.creator] = flow
        Resource.Success(true)
    }
    coEvery { deleteLocation(any(), any()) }.answers {
        val userId = firstArg<String>()
        val deleteLocation = secondArg<Location>()

        val flow = locationsFlows[userId]
            ?: MutableStateFlow(Resource.Success(emptyList()))
        flow.value = Resource.Success(flow.value.data!!.subtract(setOf(deleteLocation)).toList())
        locationsFlows[userId] = flow

        Resource.Success(true)
    }

    coEvery { saveLocations(any(), any()) }.answers {
        val userId = firstArg<String>()
        val saveLocations = secondArg<List<Location>>().associateBy { it.uuid }

        val flow = locationsFlows[userId]
            ?: MutableStateFlow(Resource.Success(emptyList()))
        val newList = flow.value.data!!.toMutableList()
            .apply { replaceAll { loc -> saveLocations[loc.uuid] ?: loc } }
        flow.value = Resource.Success(newList)
        locationsFlows[userId] = flow

        Resource.Success()
    }
}

fun LocationRemoteDao.mockErrorLocationDao(
    getLocationsError: Resource.Error<List<Location>>? = null,
    addLocationError: Resource.Error<Boolean>? = null,
    deleteLocationError: Resource.Error<Boolean>? = null,
    saveLocationError: Resource.Error<Unit>? = null,
) {
    if (getLocationsError != null)
        coEvery { getLocations(any()) }.answers { flowOf(getLocationsError) }
    if (addLocationError != null)
        coEvery { addLocation(any()) }.answers { addLocationError }
    if (deleteLocationError != null)
        coEvery { deleteLocation(any(), any()) }.answers { deleteLocationError }
    if (saveLocationError != null)
        coEvery { saveLocations(any(), any()) }.answers { saveLocationError }
}