package de.bitb.pantryplaner.usecase.item

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.UserRepository

class LoadDataUC(
    private val userRepo: UserRepository,
) { //TODO make userUsecase
    suspend operator fun invoke(): Resource<Boolean> {
        return tryIt {
//            val userLoggedInResp = userRepo.isUserLoggedIn()
//            if (userLoggedInResp is Resource.Error) {
//                return@tryIt userLoggedInResp
//            }
            val loginUserResp = userRepo.loginUser()
            if (loginUserResp is Resource.Error) {
                return@tryIt loginUserResp.castTo()
            }

            Resource.Success(true)
        }
    }
}