package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.model.CheckItem
import kotlinx.coroutines.flow.first

class AddItemsUC(
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
                if (getResp is Resource.Error) {
                    return@tryIt getResp.castTo(false)
                }

                val checklist = getResp.data!!.first()
                val items = itemIds
                    .filter { item ->
                        checklist.items.firstOrNull { it.uuid == item } == null
                    }.map { CheckItem(it) }
                val saveChecklist = checklist.copy(
                    items = setOf(
                        *items.toTypedArray(),
                        *checklist.items.toTypedArray()
                    ).toMutableList()
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