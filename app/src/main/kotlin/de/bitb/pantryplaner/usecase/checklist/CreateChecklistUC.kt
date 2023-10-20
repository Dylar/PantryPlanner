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

class CreateChecklistUC(
    private val userRepo: UserRepository,
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(
        checklist: Checklist
    ): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                val name = checklist.name
                if (name.isBlank()) {
                    return@tryIt "Name darf nicht leer sein".asResourceError()
                }
                if (checklist.stock.isBlank()) {
                    return@tryIt "Checkliste muss einem Lager zugeordnet sein".asResourceError()
                }

                val user = userRepo.getUser().first()
                if (user is Resource.Error) return@tryIt user.castTo(false)

                val check = checklist.copy(
                    name = name.capitalizeFirstCharacter(),
                    creator = user.data!!.uuid,
                )
                checkRepo.addChecklist(check)
            },
        )
    }
}