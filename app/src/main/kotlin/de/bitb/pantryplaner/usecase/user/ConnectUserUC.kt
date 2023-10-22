package de.bitb.pantryplaner.usecase.user

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.UserRepository
import kotlinx.coroutines.flow.first

class ConnectUserUC(
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(email: String): Resource<Unit> {
        return tryIt {
            val userResp = userRepo.getUser().first()
            if (userResp is Resource.Error) {
                return@tryIt userResp.castTo()
            }
            val user = userResp.data!!
            if (user.email == email) {
                return@tryIt "Such dir Freunde".asResourceError()
            }

            val newUserResp = userRepo.getUserByEmail(email)
            if (newUserResp is Resource.Error) {
                return@tryIt newUserResp.castTo()
            }
            val newUser = newUserResp.data!!
            val newUserId = newUser.uuid

            if (user.connectedUser.contains(newUserId)) {
                return@tryIt "Benutzer schon verbunden".asResourceError()
            }

            user.connectedUser.add(newUserId)
            val saveUserResp = userRepo.saveUser(user)
            if (saveUserResp is Resource.Error) saveUserResp.castTo()
            else Resource.Success()
        }
    }
}