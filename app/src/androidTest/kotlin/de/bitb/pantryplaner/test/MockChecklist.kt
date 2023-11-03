package de.bitb.pantryplaner.test

import de.bitb.pantryplaner.core.createFlows
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.parsePOKO
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.source.CheckRemoteDao
import io.mockk.coEvery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

fun parseChecklistCreator(): Checklist = parsePOKO("checklist_creator")
fun parseChecklistShared(): Checklist = parsePOKO("checklist_shared")
fun parseChecklistFinished(): Checklist = parsePOKO("checklist_finished")

@OptIn(ExperimentalCoroutinesApi::class)
fun CheckRemoteDao.mockChecklistDao(
    checks: List<Checklist> = emptyList()
) {
    val allFlow = MutableStateFlow(checks)
    val checksFlows = createFlows(checks) { check -> (listOf(check.creator) + check.sharedWith) }

    coEvery { getCheckLists(any(), any()) }.answers {
        val userId = firstArg<String>()
        val uuids = secondArg<List<String>?>()

        val flow = checksFlows[userId] ?: MutableStateFlow(Result.Success(emptyList()))
        checksFlows[userId] = flow

        allFlow.flatMapLatest { checksList ->
            flow.apply {
                value = Result.Success(
                    checksList
                        .filter { uuids?.contains(it.uuid) ?: true }
                        .filter { it.creator == userId || it.sharedWith.contains(userId) }
                )
            }
        }
    }
    coEvery { addChecklist(any()) }.answers {
        val addChecklist = firstArg<Checklist>()
        allFlow.value = allFlow.value + listOf(addChecklist)
        Result.Success(true)
    }

    coEvery { deleteChecklist(any()) }.answers {
        val deleteChecklist = firstArg<Checklist>()
        allFlow.value = allFlow.value - setOf(deleteChecklist)
        Result.Success(true)
    }

    coEvery { saveChecklist(any()) }.answers {
        val saveChecklist = firstArg<Checklist>()
        allFlow.value = allFlow.value
            .map { if (it.uuid == saveChecklist.uuid) saveChecklist else it }
        Result.Success()
    }
}

// TODO test errors
fun CheckRemoteDao.mockErrorChecklistDao(
    getChecklistsError: Result.Error<List<Checklist>>? = null,
    addChecklistError: Result.Error<Boolean>? = null,
    deleteChecklistError: Result.Error<Boolean>? = null,
    saveChecklistError: Result.Error<Unit>? = null,
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