package de.bitb.pantryplaner.test

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.parsePOKO
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.data.source.UserRemoteDao
import io.mockk.coEvery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

const val defaultPW = "1Password!"
fun parseUser(): User = parsePOKO("user_peter_lustig")
fun parseUserConnected(): User = parsePOKO("user_mohammed_lee")
fun parseUserOther(): User = parsePOKO("user_andre_option")
fun parseUserExcludie(): User = parsePOKO("user_excludie_yellow")

@OptIn(ExperimentalCoroutinesApi::class)
fun UserRemoteDao.mockUserDao(
    allUser: List<User> = listOf(),
    emailPwMap: MutableMap<String, String> = mutableMapOf(),
) {
    val scope = CoroutineScope(Dispatchers.IO)
    val allFlow = MutableSharedFlow<List<User>>(1).apply { tryEmit(allUser) }
    fun allFlowValue() = allFlow.replayCache.first()

    var loggedInWith: String? = null
    coEvery { isUserLoggedIn() }.answers { Result.Success(loggedInWith != null) }
    coEvery { registerUser(any(), any()) }.answers {
        val email = firstArg<String>()
        val pw = secondArg<String>()
        val userExists = allFlowValue().firstOrNull { it.email == email } != null
        if (userExists) return@answers "User exists".asError()
        emailPwMap[email] = pw
        loggedInWith = email
        Result.Success()
    }
    coEvery { loginUser(any(), any()) }.answers {
        val email = firstArg<String>()
        val pw = secondArg<String>()
        if (emailPwMap[email] == pw) {
            loggedInWith = email
        }
        Result.Success(loggedInWith != null)
    }
    coEvery { logoutUser() }.answers {
        loggedInWith = null
        Result.Success()
    }
    coEvery { getUser(any()) }.answers {
        val uuids = firstArg<List<String>>()
        allFlow.flatMapLatest { users ->
            val user = allFlowValue().first { it.email == loggedInWith }
            MutableStateFlow(Result.Success(
                if (uuids.size == 1 && uuids.first() == user.uuid) {
                    users.filter { it.uuid == user.uuid }
                } else {
                    users.filter { uuids.contains(it.uuid) }
                }
            ))
        }
    }
    coEvery { getUserByEmail(any()) }.answers {
        val email = firstArg<String>().lowercase()
        val allFlowValue = allFlowValue()
        val user = allFlowValue.firstOrNull { it.email.lowercase() == email }
        Result.Success(user)
    }
    coEvery { saveUser(any()) }.answers {
        val saveUser = firstArg<User>()
        val oldData = allFlowValue()
        val userExists = oldData.any { it.uuid == saveUser.uuid }
        scope.launch {
            allFlow.emit(
                if (userExists) oldData.map { if (it.uuid == saveUser.uuid) saveUser else it }
                else oldData + saveUser
            )
        }
        Result.Success()
    }
}

// TODO test errors
fun UserRemoteDao.mockErrorUserDao(
    isLoggedInError: Result.Error<Boolean>? = null,
    registerError: Result.Error<Unit>? = null,
    loginError: Result.Error<Boolean>? = null,
    logoutError: Result.Error<Unit>? = null,
    getUserError: Result.Error<List<User>>? = null,
    getUserByEmailError: Result.Error<User>? = null,
    saveUserError: Result.Error<Unit>? = null,
) {
    if (isLoggedInError != null)
        coEvery { isUserLoggedIn() }.answers { isLoggedInError }
    if (registerError != null)
        coEvery { registerUser(any(), any()) }.answers { registerError }
    if (loginError != null)
        coEvery { loginUser(any(), any()) }.answers { loginError }
    if (logoutError != null)
        coEvery { logoutUser() }.answers { logoutError }
    if (getUserError != null)
        coEvery { getUser(any()) }.answers { flowOf(getUserError) }
    if (getUserByEmailError != null)
        coEvery { getUserByEmail(any()) }.answers { getUserByEmailError }
    if (saveUserError != null)
        coEvery { saveUser(any()) }.answers { saveUserError }
}