package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.User
import kotlinx.coroutines.flow.first

class SetSharedWithUC(
    private val userRepo: UserRepository,
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(checkId: String, sharedWith: List<User>): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                val getResp = checkRepo.getCheckLists(listOf(checkId)).first()
                if (getResp is Resource.Error) {
                    return@tryIt getResp.castTo(false)
                }

                val checklist = getResp.data!!.first()

                val user = userRepo.getUser().first()
                if (user is Resource.Error) return@tryIt user.castTo()
                if (user.data!!.uuid != checklist.creator)
                    return@tryIt "Nur der Ersteller kann die Checklist ändern".asResourceError()

                val saveChecklist = checklist.copy(sharedWith = sharedWith.map { it.uuid })
                val saveResp = checkRepo.saveChecklist(saveChecklist)
                if (saveResp is Resource.Error) return@tryIt saveResp.castTo(false)
                else Resource.Success(true)
            },
        )
    }
}