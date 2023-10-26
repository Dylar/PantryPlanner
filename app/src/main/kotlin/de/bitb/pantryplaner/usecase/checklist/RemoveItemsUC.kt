package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Logger
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import kotlinx.coroutines.flow.first

class RemoveItemsUC(
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(checkId: String, itemIds: List<String>): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                if (itemIds.isEmpty()) {
                    return@tryIt "Keine Items ausgewählt".asResourceError()
                }

                val checkResp = checkRepo.getCheckLists(listOf(checkId)).first()
                if (checkResp is Resource.Error) return@tryIt checkResp.castTo(false)

                val checklist = checkResp.data!!.first()
                val items = checklist.items.filter { !itemIds.contains(it.uuid) }.toMutableList()

                val saveResp = checkRepo.saveChecklist(checklist.copy(items = items))
                if (saveResp is Resource.Error) return@tryIt saveResp.castTo(false)

                Resource.Success(true)
            },
        )
    }
}