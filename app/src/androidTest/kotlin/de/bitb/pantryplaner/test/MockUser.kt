package de.bitb.pantryplaner.test

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.parsePOKO
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.data.source.UserRemoteDao
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf

const val defaultPW = "1Password!"
fun parseUser(): User = parsePOKO("user_peter_lustig")
fun parseUserConnected(): User = parsePOKO("user_mohammed_lee")
fun parseUserOther(): User = parsePOKO("user_andre_option")
fun parseUserExcludie(): User = parsePOKO("user_excludie_yellow")

fun UserRemoteDao.mockUserDao(
    allUser: MutableList<User> = mutableListOf(),
    emailPwMap: MutableMap<String, String> = mutableMapOf(),
) {
    var isLoggedIn = false
    coEvery { isUserLoggedIn() }.answers { Resource.Success(isLoggedIn) }
    coEvery { registerUser(any(), any()) }.answers {
        val email = firstArg<String>()
        val pw = secondArg<String>()
        val userExists = allUser.firstOrNull { it.email == email } != null
        if (userExists) "User exists".asResourceError()
        else Resource.Success<Unit>().also {
            allUser.add(User(email = email))
            emailPwMap[email] = pw
        }
    }
    coEvery { loginUser(any(), any()) }.answers {
        val email = firstArg<String>()
        val pw = secondArg<String>()
        isLoggedIn = emailPwMap[email] == pw
        Resource.Success(isLoggedIn)
    }
    coEvery { logoutUser() }.answers { Resource.Success() }
    coEvery { getUser(any()) }.answers {
        val uuids = firstArg<List<String>>()
        val users = allUser.filter { uuids.contains(it.uuid) }
        flowOf(Resource.Success(users))
    }
    coEvery { getUserByEmail(any()) }.answers {
        val email = firstArg<String>()
        val users = allUser.first { it.email == email }
        Resource.Success(users)
    }
    coEvery { saveUser(any()) }.answers {
        val saveUser = firstArg<User>()
        allUser.replaceAll { if (it.email == saveUser.email) saveUser else it }
        Resource.Success()
    }
}

// TODO test errors
fun UserRemoteDao.mockErrorUserDao(
    isLoggedInError: Resource.Error<Boolean>? = null,
    registerError: Resource.Error<Unit>? = null,
    loginError: Resource.Error<Boolean>? = null,
    logoutError: Resource.Error<Unit>? = null,
    getUserError: Resource.Error<List<User>>? = null,
    getUserByEmailError: Resource.Error<User>? = null,
    saveUserError: Resource.Error<Unit>? = null,
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