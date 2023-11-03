package de.bitb.pantryplaner.usecase.user

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.UserRepository

class LogoutUC(
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return tryIt {
            userRepo.logoutUser()
        }
    }
}