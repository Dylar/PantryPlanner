package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import kotlinx.coroutines.flow.first

class RemoveItemsUC(
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(checkId: String, itemIds: List<String>): Result<Boolean> {
        return tryIt(
            onError = { Result.Error(it, false) },
            onTry = {
                if (itemIds.isEmpty()) {
                    return@tryIt "Keine Items ausgewählt".asError()
                }

                val checkResp = checkRepo.getCheckLists(listOf(checkId)).first()
                if (checkResp is Result.Error) return@tryIt checkResp.castTo(false)

                val checklist = checkResp.data!!.first()
                val items = checklist.items.filter { !itemIds.contains(it.uuid) }.toMutableList()

                val saveResp = checkRepo.saveChecklist(checklist.copy(items = items))
                if (saveResp is Result.Error) return@tryIt saveResp.castTo(false)

                Result.Success(true)
            },
        )
    }
}