package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import kotlinx.coroutines.flow.first

class CheckItemUC(
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(checkId: String, itemId: String): Resource<Unit> {
        return tryIt {
            val getResp = checkRepo.getCheckLists(listOf(checkId)).first()
            if (getResp is Resource.Error) {
                return@tryIt getResp.castTo()
            }

            val checklist = getResp.data!!.first()
            val checked = checklist.checked
            if (checked.remove(itemId)) {
                checked.add((itemId))
            }
            val saveChecklist = checklist.copy(checked = checked)

            val saveResp = checkRepo.saveChecklist(saveChecklist)
            if (saveResp is Resource.Error) {
                return@tryIt saveResp.castTo()
            }
            Resource.Success()
        }
    }
}