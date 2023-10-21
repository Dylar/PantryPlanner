package de.bitb.pantryplaner.usecase.user

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.User
import kotlinx.coroutines.flow.first

class DisconnectUserUC(
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(removeUser: User): Resource<Unit> {
        return tryIt {
            val userResp = userRepo.getUser().first()
            if (userResp is Resource.Error) {
                return@tryIt userResp.castTo()
            }

            val user = userResp.data!!
            user.connectedUser.remove(removeUser.uuid)
            val saveUserResp = userRepo.saveUser(user)
            if (saveUserResp is Resource.Error) saveUserResp.castTo()
            else Resource.Success()
        }
    }
}