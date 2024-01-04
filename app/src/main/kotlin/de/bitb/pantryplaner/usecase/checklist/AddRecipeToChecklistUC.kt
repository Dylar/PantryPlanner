package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Recipe

class AddRecipeToChecklistUC(
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(checklist: Checklist, recipe: Recipe): Result<Boolean> {
        return tryIt(
            onError = { Result.Error(it, false) },
            onTry = {
                val items = recipe.items
                if (items.isEmpty()) {
                    return@tryIt "Rezept hat keine Zutaten".asError()
                }

                val saveItems = items
                    .map { item ->
                        val saveItem = checklist.items.firstOrNull { it.uuid == item.uuid }
                        saveItem?.copy(amount = saveItem.amount + item.amount) ?: item.toCheckItem()
                    }
                val restItems = saveItems
                    .filter { item -> checklist.items.none { it.uuid == item.uuid } }
                val saveChecklist = checklist.copy(
                    items = setOf(
                        *restItems.toTypedArray(),
                        *saveItems.toTypedArray()
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