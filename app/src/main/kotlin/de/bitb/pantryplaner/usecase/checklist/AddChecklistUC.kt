package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.capitalizeFirstCharacter
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.CheckItem
import de.bitb.pantryplaner.data.model.Checklist
import kotlinx.coroutines.flow.first

class AddChecklistUC(
    private val checkRepo: CheckRepository,
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(
        name: String,
        items: List<String> = emptyList(),
        sharedWith: List<String> = emptyList(),
    ): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                if (name.isBlank()) {
                    return@tryIt "Name darf nicht leer sein".asResourceError()
                }

                val user = userRepo.getUser().first()
                if (user is Resource.Error) return@tryIt user.castTo(false)

                val checkItems = items.map { CheckItem(it) }.toMutableList()
                val check = Checklist(
                    name = name.capitalizeFirstCharacter(),
                    items = checkItems,
                    creator = user.data!!.uuid,
                    sharedWith = sharedWith,
                )
                checkRepo.addChecklist(check)
            },
        )
    }
}