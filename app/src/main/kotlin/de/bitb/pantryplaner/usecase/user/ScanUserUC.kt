package de.bitb.pantryplaner.usecase.user

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.UserRepository

class ScanUserUC(
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(uuid: String): Resource<Unit> {
        return tryIt {
            val currentUserResp = userRepo.getUser()
            if (currentUserResp is Resource.Error) {
                return@tryIt currentUserResp.castTo()
            }

            val currentUser = currentUserResp.data!!
            if (currentUser.uuid == uuid || currentUser.connectedUser.contains(uuid)) {
                return@tryIt "Benutzer schon verbunden".asResourceError()
            }

            val userResp = userRepo.getUser(uuid)
            if (userResp is Resource.Error) {
                return@tryIt userResp.castTo()
            }

            val user = userResp.data!!
            user.connectedUser.add(uuid)
            val saveUserResp = userRepo.saveUser(user)
            if (saveUserResp is Resource.Error) saveUserResp.castTo()
            else Resource.Success()
        }
    }
}