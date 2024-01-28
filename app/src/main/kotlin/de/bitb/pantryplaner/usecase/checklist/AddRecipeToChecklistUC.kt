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

                val updatedItems = checklist.items.map { item ->
                    items.firstOrNull { it.uuid == item.uuid }
                        ?.let { item.copy(amount = item.amount + it.amount) }
                        ?: item
                }.toMutableList()

                items.forEach { item ->
                    if (checklist.items.none { it.uuid == item.uuid }) {
                        updatedItems.add(item.toCheckItem())
                    }
                }

                val saveChecklist = checklist.copy(items = updatedItems)
                val saveResp = checkRepo.saveChecklist(saveChecklist)
                if (saveResp is Result.Error) {
                    return@tryIt saveResp.castTo(false)
                }
                Result.Success(true)
            },
        )
    }
}