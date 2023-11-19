package de.bitb.pantryplaner.usecase.user

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.User
import kotlinx.coroutines.flow.first

class DisconnectUserUC(
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(removeUser: User): Result<Unit> {
        return tryIt {
            val userResp = userRepo.getUser().first()
            if (userResp is Result.Error) {
                return@tryIt userResp.castTo()
            }

            // TODO disconnect both ?
            val user = userResp.data!!
            val conUser =  user.connectedUser.toMutableList().apply { remove(removeUser.uuid) }
            val saveUserResp = userRepo.saveUser(user.copy(connectedUser = conUser))
            if (saveUserResp is Result.Error) saveUserResp.castTo()
            else Result.Success()
        }
    }
}