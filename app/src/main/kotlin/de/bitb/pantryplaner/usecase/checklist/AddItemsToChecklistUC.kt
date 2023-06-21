package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import kotlinx.coroutines.flow.first

class AddItemsToChecklistUC(
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(checkId: String, itemIds: List<String>): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                val getResp = checkRepo.getCheckLists(listOf(checkId)).first()
                if (getResp is Resource.Error) {
                    return@tryIt getResp.castTo(false)
                }

                val checklist = getResp.data!!.first()
                val saveChecklist = checklist.copy(
                    items = setOf(
                        *itemIds.toTypedArray(),
                        *checklist.items.toTypedArray()
                    ).toList()
                )

                val saveResp = checkRepo.saveChecklist(saveChecklist)
                if (saveResp is Resource.Error) {
                    return@tryIt saveResp.castTo(false)
                }
                Resource.Success(true)
            },
        )
    }
}