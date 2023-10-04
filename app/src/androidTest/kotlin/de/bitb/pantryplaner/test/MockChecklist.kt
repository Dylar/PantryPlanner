package de.bitb.pantryplaner.test

import de.bitb.pantryplaner.core.createFlows
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.parsePOKO
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.source.CheckRemoteDao
import io.mockk.coEvery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

fun parseChecklistCreator(): Checklist = parsePOKO("checklist_creator")
fun parseChecklistShared(): Checklist = parsePOKO("checklist_shared")

fun CheckRemoteDao.mockChecklistDao(
    checks: List<Checklist> = emptyList()
) {
    val checksFlows = createFlows(checks) { check -> (listOf(check.creator) + check.sharedWith) }
    val checksMap = checksFlows
        .mapValues { it.value.value.data?.toMutableList() ?: mutableListOf() }
        .toMutableMap()

    coEvery { getCheckLists(any(), any()) }.answers {
        val userId = firstArg<String>()
        val uuids = secondArg<List<String>?>() ?: emptyList()

        val userList = checksMap[userId] ?: mutableListOf()
        val flow = checksFlows[userId] ?: MutableStateFlow(Resource.Success(emptyList()))
        checksFlows[userId] = flow

        val list = if (uuids.isEmpty()) userList else userList.filter { uuids.contains(it.uuid) }
        flow.value = Resource.Success(list)
        flow
    }
    coEvery { addChecklist(any()) }.answers {
        val addChecklist = firstArg<Checklist>()
        val userId = addChecklist.creator

        val userList = checksMap[userId] ?: mutableListOf()
        userList.add(addChecklist)

        val flow = checksFlows[userId]
            ?: MutableStateFlow(Resource.Success(emptyList()))
        checksFlows[userId] = flow

        val oldData = flow.value.data!!
        flow.value = Resource.Success(
            userList.filter { oldData.firstOrNull { old -> old.uuid == it.uuid } != null }
        )

        Resource.Success(true)
    }

    coEvery { deleteChecklist(any()) }.answers {
        val deleteChecklist = firstArg<Checklist>()
        val userId = deleteChecklist.creator

        val userList = checksMap[userId] ?: mutableListOf()
        userList.remove(deleteChecklist)

        val flow = checksFlows[userId]
            ?: MutableStateFlow(Resource.Success(emptyList()))
        checksFlows[userId] = flow

        val oldData = flow.value.data!!
        flow.value = Resource.Success(
            userList.filter { oldData.firstOrNull { old -> old.uuid == it.uuid } != null }
        )

        Resource.Success(true)
    }

    coEvery { saveChecklist(any()) }.answers {
        val saveChecklist = firstArg<Checklist>()
        checksFlows.forEach { (userId, flow) ->
            val userList = checksMap[userId] ?: mutableListOf()
            val newList = userList
                .apply { replaceAll { check -> if (saveChecklist.uuid == check.uuid) saveChecklist else check } }
                .filter { it.creator == userId || it.sharedWith.contains(userId) }

            checksMap[userId] = newList.toMutableList()
            flow.value = Resource.Success(newList)
        }

        Resource.Success()
    }
}

// TODO test errors
fun CheckRemoteDao.mockErrorChecklistDao(
    getChecklistsError: Resource.Error<List<Checklist>>? = null,
    addChecklistError: Resource.Error<Boolean>? = null,
    deleteChecklistError: Resource.Error<Boolean>? = null,
    saveChecklistError: Resource.Error<Unit>? = null,
) {
    if (getChecklistsError != null)
        coEvery { getCheckLists(any(), any()) }.answers { flowOf(getChecklistsError) }
    if (addChecklistError != null)
        coEvery { addChecklist(any()) }.answers { addChecklistError }
    if (deleteChecklistError != null)
        coEvery { deleteChecklist(any()) }.answers { deleteChecklistError }
    if (saveChecklistError != null)
        coEvery { saveChecklist(any()) }.answers { saveChecklistError }
}