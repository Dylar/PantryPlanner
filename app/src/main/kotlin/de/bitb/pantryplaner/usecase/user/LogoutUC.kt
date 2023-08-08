package de.bitb.pantryplaner.usecase.user

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.UserRepository

class LogoutUC(
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(): Resource<Unit> {
        return tryIt {
//            val logoutResp = userRepo.logoutUser()
//            if (logoutResp is Resource.Error) {
//                return@tryIt logoutResp
//            }
            userRepo.logoutUser()
        }
    }
}