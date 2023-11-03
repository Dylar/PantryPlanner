package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import kotlinx.coroutines.flow.first

class CheckItemUC(
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(checkId: String, itemId: String): Result<Unit> {
        return tryIt {
            val getResp = checkRepo.getCheckLists(listOf(checkId)).first()
            if (getResp is Result.Error) return@tryIt getResp.castTo()

            val checklist = getResp.data!!.first()
            val items = checklist.items.toMutableList()
            val item = items.first { it.uuid == itemId }
            item.checked = !item.checked

            val saveResp = checkRepo.saveChecklist(checklist)
            if (saveResp is Result.Error) return@tryIt saveResp.castTo()

            Result.Success()
        }
    }
}