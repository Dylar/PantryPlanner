package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Item

class RemoveChecklistUC(
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(check: Checklist): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = { checkRepo.removeChecklist(check) },
        )
    }
}