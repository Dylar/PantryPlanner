package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Checklist
import kotlinx.coroutines.flow.first

class SaveChecklistUC(
    private val userRepo: UserRepository,
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(checklist: Checklist): Result<Boolean> {
        return tryIt(
            onError = { Result.Error(it, false) },
            onTry = {
                val user = userRepo.getUser().first()
                if (user is Result.Error) return@tryIt user.castTo()
                if (user.data!!.uuid != checklist.creator)
                    return@tryIt "Nur der Ersteller kann die Checklist ändern".asError()

                val saveResp = checkRepo.saveChecklist(checklist)
                if (saveResp is Result.Error) return@tryIt saveResp.castTo(false)
                else Result.Success(true)
            },
        )
    }
}