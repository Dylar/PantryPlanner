package de.bitb.pantryplaner.usecase.user

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.User
import kotlinx.coroutines.flow.first

class DisconnectUserUC(
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(user: User): Resource<Unit> {
        return tryIt {
            val currentUserResp = userRepo.getUser().first()
            if (currentUserResp is Resource.Error) {
                return@tryIt currentUserResp.castTo()
            }

            val currentUser = currentUserResp.data!!
            currentUser.connectedUser.remove(user.uuid)
            val saveUserResp = userRepo.saveUser(currentUser)
            if (saveUserResp is Resource.Error) saveUserResp.castTo()
            else Resource.Success()
        }
    }
}