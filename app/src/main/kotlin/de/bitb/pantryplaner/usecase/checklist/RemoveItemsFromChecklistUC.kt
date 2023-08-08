package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import kotlinx.coroutines.flow.first

class RemoveItemsFromChecklistUC(
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(checkId: String, itemIds: List<String>): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                if (itemIds.isEmpty()) {
                    return@tryIt "Keine Items ausgewählt".asResourceError()
                }

                val getResp = checkRepo.getCheckLists(listOf(checkId)).first()
                if (getResp is Resource.Error) return@tryIt getResp.castTo(false)

                val checklist = getResp.data!!.first()
                val items = checklist.items.filter { !itemIds.contains(it.uuid) }.toMutableList()
                val saveChecklist = checklist.copy(items = items)

                val saveResp = checkRepo.saveChecklist(saveChecklist)
                if (saveResp is Resource.Error) return@tryIt saveResp.castTo(false)

                Resource.Success(true)
            },
        )
    }
}