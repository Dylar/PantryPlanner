package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) {
    suspend fun isUserLoggedIn(): Result<Boolean> {
        return remoteDB.isUserLoggedIn()
    }

    suspend fun registerUser(user: User, pw: String): Result<Unit> {
        return tryIt {
            val resp = remoteDB.registerUser(user.email, pw)
            if (resp is Result.Error) return@tryIt resp

            localDB.setUser(user.uuid)
            remoteDB.saveUser(user)
        }
    }

    suspend fun loginUser(email: String, pw: String): Result<User> {
        return tryIt {
            val resp = remoteDB.loginUser(email, pw)
            if (resp is Result.Error) return@tryIt resp.castTo()

            val user = getUserByEmail(email)
            if (user is Result.Error) return@tryIt user

            localDB.setUser(user.data!!.uuid)
            user
        }
    }

    suspend fun logoutUser(): Result<Unit> {
        return tryIt {
            val resp = remoteDB.logoutUser()
            if (resp is Result.Error) return@tryIt resp

            localDB.setUser("")
            resp
        }
    }

    fun getUser(): Flow<Result<User>> {
        return getUser(localDB.getUser())
    }

    fun getUser(uuid: String): Flow<Result<User>> {
        return getUser(listOf(uuid)).map { resp ->
            if (resp is Result.Error) resp.castTo()
            else Result.Success(resp.data!!.first())
        }
    }//TODO getUser to watchUser

    fun getUser(uuids: List<String>): Flow<Result<List<User>>> {
        return if (uuids.isEmpty()) MutableStateFlow(Result.Success(emptyList()))
        else remoteDB.getUser(uuids)
            .map { resp ->
                if (resp is Result.Error) return@map resp.castTo()
                if (resp.data?.isEmpty() != false) return@map "Benutzer nicht gefunden: $uuids".asError()
                else Result.Success(resp.data)
            }
    }

    suspend fun getUserByEmail(email: String): Result<User> {
        return tryIt {
            val user = remoteDB.getUserByEmail(email)
            when {
                user is Result.Error -> user
                user.data == null -> "Benutzer nicht gefunden".asError()
                else -> user
            }
        }
    }

    suspend fun saveUser(user: User): Result<User> {
        return tryIt {
            val resp = remoteDB.saveUser(user)
            if (resp is Result.Error) resp.castTo()
            else Result.Success(user)
        }
    }

}
