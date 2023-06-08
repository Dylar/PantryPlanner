package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.source.RemoteService

interface UserRepository {
    suspend fun loginUser(): Resource<Unit>
}

class UserRepositoryImpl constructor(
    private val remoteDB: RemoteService,
) : UserRepository {

    override suspend fun loginUser(): Resource<Unit> {
        return tryIt {
            val resp = remoteDB.loginUser()
            if (resp is Resource.Error) {
                resp.castTo()
            } else {
                Resource.Success()
            }
        }
    }
}
