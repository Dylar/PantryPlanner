package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import kotlinx.coroutines.flow.first

class FinishChecklistUC(
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(checkId: String): Resource<Unit> {
        return tryIt {
            val getResp = checkRepo.getCheckLists(listOf(checkId)).first()
            if (getResp is Resource.Error) {
                return@tryIt getResp.castTo()
            }

            val checklist = getResp.data!!.first()
            val saveChecklist = checklist.copy(finished = true)
            val saveResp = checkRepo.saveChecklist(saveChecklist)
            if (saveResp is Resource.Error) {
                return@tryIt saveResp.castTo()
            }

            // TODO add items to stock
            Resource.Success()
        }
    }
}