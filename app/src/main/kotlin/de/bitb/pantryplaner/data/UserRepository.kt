package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.data.source.RemoteService

interface UserRepository {
    suspend fun isUserLoggedIn(): Resource<Boolean>
    suspend fun registerUser(email: String, pw: String): Resource<Unit>
    suspend fun loginUser(email: String, pw: String): Resource<User>
    suspend fun logoutUser(): Resource<Unit>
    suspend fun getUser(uuid: String): Resource<User>
    suspend fun saveUser(user: User): Resource<User>
}

class UserRepositoryImpl constructor(
    private val remoteDB: RemoteService,
) : UserRepository {
    override suspend fun isUserLoggedIn(): Resource<Boolean> {
        return remoteDB.isUserLoggedIn();
    }

    override suspend fun registerUser(email: String, pw: String): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun loginUser(email: String, pw: String): Resource<User> {
        return tryIt {
            val resp = remoteDB.loginUser()
            if (resp is Resource.Error) resp.castTo()
            else Resource.Success()
        }
    }

    override suspend fun logoutUser(): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(uuid: String): Resource<User> {
        TODO("Not yet implemented")
    }

    override suspend fun saveUser(user: User): Resource<User> {
        TODO("Not yet implemented")
    }

}
