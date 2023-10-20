package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

interface UserRepository { //TODO remove repo interface
    suspend fun isUserLoggedIn(): Resource<Boolean>
    suspend fun registerUser(user: User, pw: String): Resource<Unit>
    suspend fun loginUser(email: String, pw: String): Resource<User>
    suspend fun logoutUser(): Resource<Unit>
    fun getUser(): Flow<Resource<User>>
    fun getUser(uuid: String): Flow<Resource<User>>
    fun getUser(uuids: List<String>): Flow<Resource<List<User>>>
    suspend fun saveUser(user: User): Resource<User>
}

class UserRepositoryImpl(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) : UserRepository {
    override suspend fun isUserLoggedIn(): Resource<Boolean> {
        return remoteDB.isUserLoggedIn()
    }

    override suspend fun registerUser(user: User, pw: String): Resource<Unit> {
        return tryIt {
            val resp = remoteDB.registerUser(user.email, pw)
            if (resp is Resource.Error) return@tryIt resp

            localDB.setUser(user.uuid)
            remoteDB.saveUser(user)
        }
    }

    override suspend fun loginUser(email: String, pw: String): Resource<User> {
        return tryIt {
            val resp = remoteDB.loginUser(email, pw)
            if (resp is Resource.Error) return@tryIt resp.castTo()

            val user = remoteDB.getUserByEmail(email)
            if (user is Resource.Error) return@tryIt user
            if (user.data == null) return@tryIt "Benutzer nicht gefunden".asResourceError()

            localDB.setUser(user.data.uuid)
            user
        }
    }

    override suspend fun logoutUser(): Resource<Unit> {
        return tryIt {
            val resp = remoteDB.logoutUser()
            if (resp is Resource.Error) return@tryIt resp

            localDB.setUser("")
            resp
        }
    }

    override fun getUser(): Flow<Resource<User>> {
        return getUser(localDB.getUser())
    }

    override fun getUser(uuid: String): Flow<Resource<User>> {
        return getUser(listOf(uuid)).map { resp ->
            if (resp is Resource.Error) resp.castTo()
            else Resource.Success(resp.data!!.first())
        }
    }

    override fun getUser(uuids: List<String>): Flow<Resource<List<User>>> {
        return if (uuids.isEmpty()) MutableStateFlow(Resource.Success(emptyList()))
        else remoteDB.getUser(uuids)
            .map { resp ->
                if (resp is Resource.Error) return@map resp.castTo()
                if (resp.data?.isEmpty() != false) return@map "Benutzer nicht gefunden: $uuids".asResourceError()
                else Resource.Success(resp.data)
            }
    }

    override suspend fun saveUser(user: User): Resource<User> {
        return tryIt {
            val resp = remoteDB.saveUser(user)
            if (resp is Resource.Error) resp.castTo()
            else Resource.Success(user)
        }
    }

}
