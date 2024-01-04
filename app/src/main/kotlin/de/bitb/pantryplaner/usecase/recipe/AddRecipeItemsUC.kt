package de.bitb.pantryplaner.usecase.recipe

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.model.CheckItem
import kotlinx.coroutines.flow.first

class AddRecipeItemsUC(
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(checkId: String, itemIds: List<String>): Result<Boolean> {
        return tryIt(
            onError = { Result.Error(it, false) },
            onTry = {
                if (itemIds.isEmpty()) {
                    return@tryIt "Keine Items ausgewählt".asError()
                }

                val getResp = checkRepo.getCheckLists(listOf(checkId)).first()
                if (getResp is Result.Error) {
                    return@tryIt getResp.castTo(false)
                }

                val checklist = getResp.data!!.first()
                val items = itemIds
                    .filter { item -> checklist.items.firstOrNull { it.uuid == item } == null }
                    .map { CheckItem(it) }
                val saveChecklist = checklist.copy(
                    items = setOf(
                        *items.toTypedArray(),
                        *checklist.items.toTypedArray()
                    ).toMutableList()
                )

                val saveResp = checkRepo.saveChecklist(saveChecklist)
                if (saveResp is Result.Error) {
                    return@tryIt saveResp.castTo(false)
                }
                Result.Success(true)
            },
        )
    }
}