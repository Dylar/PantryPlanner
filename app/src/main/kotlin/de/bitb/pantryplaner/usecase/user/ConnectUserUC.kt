package de.bitb.pantryplaner.usecase.user

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.UserRepository
import kotlinx.coroutines.flow.first

class ConnectUserUC(
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return tryIt {
            val userResp = userRepo.getUser().first()
            if (userResp is Result.Error) {
                return@tryIt userResp.castTo()
            }
            val user = userResp.data!!
            if (user.email == email) {
                return@tryIt "Such dir Freunde".asError()
            }

            val newUserResp = userRepo.getUserByEmail(email)
            if (newUserResp is Result.Error) {
                return@tryIt newUserResp.castTo()
            }
            val newUser = newUserResp.data!!
            val newUserId = newUser.uuid

            if (user.connectedUser.contains(newUserId)) {
                return@tryIt "Benutzer schon verbunden".asError()
            }

            user.connectedUser.add(newUserId)
            val saveUserResp = userRepo.saveUser(user)
            if (saveUserResp is Result.Error) return@tryIt saveUserResp.castTo()

            newUser.connectedUser.add(user.uuid)
            val saveNewUserResp = userRepo.saveUser(newUser)
            if (saveNewUserResp is Result.Error) saveNewUserResp.castTo()
            else Result.Success()
        }
    }
}