package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.capitalizeFirstCharacter
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Checklist
import kotlinx.coroutines.flow.first

class CreateChecklistUC(
    private val userRepo: UserRepository,
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(
        checklist: Checklist
    ): Result<Boolean> {
        return tryIt(
            onError = { Result.Error(it, false) },
            onTry = {
                val name = checklist.name
                if (name.isBlank()) {
                    return@tryIt "Name darf nicht leer sein".asError()
                }
                if (checklist.stock.isBlank()) {
                    return@tryIt "Checkliste muss einem Lager zugeordnet sein".asError()
                }

                val user = userRepo.getUser().first()
                if (user is Result.Error) return@tryIt user.castTo(false)

                val check = checklist.copy(
                    name = name.capitalizeFirstCharacter(),
                    creator = user.data!!.uuid,
                )
                checkRepo.addChecklist(check)
            },
        )
    }
}