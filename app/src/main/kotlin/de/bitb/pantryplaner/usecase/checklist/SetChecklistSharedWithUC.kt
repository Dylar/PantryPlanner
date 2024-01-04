package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.User
import kotlinx.coroutines.flow.first

class SetChecklistSharedWithUC(
    private val userRepo: UserRepository,
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(checkId: String, sharedWith: List<User>): Result<Boolean> {
        return tryIt(
            onError = { Result.Error(it, false) },
            onTry = {
                val getResp = checkRepo.getCheckList(checkId).first()
                if (getResp is Result.Error) {
                    return@tryIt getResp.castTo(false)
                }

                val checklist = getResp.data!!
                val user = userRepo.getUser().first()
                if (user is Result.Error) return@tryIt user.castTo()
                if (user.data!!.uuid != checklist.creator)
                    return@tryIt "Nur der Ersteller kann die Checklist ändern".asError()

                val saveChecklist = checklist.copy(sharedWith = sharedWith.map { it.uuid })
                val saveResp = checkRepo.saveChecklist(saveChecklist)
                if (saveResp is Result.Error) return@tryIt saveResp.castTo(false)
                Result.Success(true)
            },
        )
    }
}