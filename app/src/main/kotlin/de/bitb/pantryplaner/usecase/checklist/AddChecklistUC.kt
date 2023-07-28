package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.model.CheckItem
import de.bitb.pantryplaner.data.model.Checklist

class AddChecklistUC(
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(
        name: String,
        items: List<String> = emptyList()
    ): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                if (name.isBlank()) {
                    return@tryIt "Name darf nicht leer sein".asResourceError()
                }
                val checkItems = items.map { CheckItem(it) }.toMutableList()
                val check = Checklist(name = name, items = checkItems)
                checkRepo.addChecklist(check)
            },
        )
    }
}