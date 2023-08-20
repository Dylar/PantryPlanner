package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Checklist
import kotlinx.coroutines.flow.first

class DeleteChecklistUC(
    private val userRepo: UserRepository,
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(check: Checklist): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                val userResp = userRepo.getUser().first()
                if (userResp is Resource.Error) return@tryIt userResp.castTo(false)

                val user = userResp.data?.uuid
                if (user == check.creator) {
                    checkRepo.removeChecklist(check)
                } else {
                    val newList = check.sharedWith.subtract(setOf(user!!))
                    checkRepo.saveChecklist(check.copy(sharedWith = newList.toList()))
                    return@tryIt Resource.Success(true)
                }
            },
        )
    }
}