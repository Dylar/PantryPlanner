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
fun parseChecklistFinished(): Checklist = parsePOKO("checklist_finished")

fun CheckRemoteDao.mockChecklistDao(
    checks: List<Checklist> = emptyList()
) {
    val checksFlows = createFlows(checks) { check -> (listOf(check.creator) + check.sharedWith) }
    val checksMap = checksFlows
        .mapValues { it.value.value.data?.toMutableList() ?: mutableListOf() }
        .toMutableMap()

    coEvery { getCheckLists(any(), any()) }.answers {
        val userId = firstArg<String>()
        val uuids = secondArg<List<String>?>()

        val checksList = checksMap[userId] ?: mutableListOf()
        val flow = checksFlows[userId] ?: MutableStateFlow(Resource.Success(emptyList()))
        checksFlows[userId] = flow

        flow.value = Resource.Success(
            if (uuids == null) checksList
            else checksList.filter { uuids.contains(it.uuid) }
        )
        flow
    }
    coEvery { addChecklist(any()) }.answers {
        val addChecklist = firstArg<Checklist>()
        val userId = addChecklist.creator

        val checksList = checksMap[userId] ?: mutableListOf()
        checksMap[userId] = checksList
        checksList.add(addChecklist)

        setFlowValue(checksFlows, userId, checksList)
        Resource.Success(true)
    }

    coEvery { deleteChecklist(any()) }.answers {
        val deleteChecklist = firstArg<Checklist>()
        val userId = deleteChecklist.creator

        val checksList = checksMap[userId] ?: mutableListOf()
        checksList.remove(deleteChecklist)

        setFlowValue(checksFlows, userId, checksList)
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

private fun setFlowValue(
    checksFlows: MutableMap<String, MutableStateFlow<Resource<List<Checklist>>>>,
    userId: String,
    checksList: MutableList<Checklist>
) {
    val flow = checksFlows[userId]
        ?: MutableStateFlow(Resource.Success(emptyList()))
    checksFlows[userId] = flow

    val oldData = flow.value.data!!
    flow.value = Resource.Success(
        if (oldData.isEmpty()) checksList.toList()
        else
            checksList.filter { oldData.firstOrNull { old -> old.uuid == it.uuid } != null }
    )
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