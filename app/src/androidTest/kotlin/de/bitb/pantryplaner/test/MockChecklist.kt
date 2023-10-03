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

    coEvery { getCheckLists(any(), any()) }.answers {
        val uuid = firstArg<String>()
        val flow = checksFlows[uuid] ?: MutableStateFlow(Resource.Success(emptyList()))
        checksFlows[uuid] = flow
        flow
    }
    coEvery { addChecklist(any()) }.answers {
        val addChecklist = firstArg<Checklist>()
        val userId = addChecklist.creator

        val flow = checksFlows[userId]
            ?: MutableStateFlow(Resource.Success(emptyList()))
        flow.value = Resource.Success(listOf(addChecklist, *flow.value.data!!.toTypedArray()))
        checksFlows[userId] = flow
        Resource.Success(true)
    }

    coEvery { deleteChecklist(any()) }.answers {
        val deleteChecklist = firstArg<Checklist>()
        val userId = deleteChecklist.creator

        val flow = checksFlows[userId]
            ?: MutableStateFlow(Resource.Success(emptyList()))
        flow.value = Resource.Success(flow.value.data!!.subtract(setOf(deleteChecklist)).toList())
        checksFlows[userId] = flow

        Resource.Success(true)
    }

    coEvery { saveChecklist(any()) }.answers {
        val saveChecklists = firstArg<Checklist>()
        checksFlows.forEach { (userId, flow) ->
            val newList = flow.value.data!!.toMutableList()
                .apply { replaceAll { check -> if (saveChecklists.uuid == check.uuid) saveChecklists else check } }
                .filter { it.creator == userId || it.sharedWith.contains(userId) }
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