package de.bitb.pantryplaner.test

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.data.source.UserRemoteDao
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf

const val defaultPW = "1Password!"

fun buildUser(
    email: String = "defaul@user.de",
    firstName: String = "Max",
    lastName: String = "Mustermann",
    connectedUser: MutableList<String> = mutableListOf(),
): User =
    User(email = email, firstName = firstName, lastName = lastName, connectedUser = connectedUser)

fun UserRemoteDao.mockDefaultUserDao() {
    val user = buildUser()
    val map = mutableMapOf(user.email to defaultPW)
    mockUserDao(emailPwMap = map, allUser = mutableListOf(user))
}

fun UserRemoteDao.mockUserDao(
    emailPwMap: MutableMap<String, String> = mutableMapOf(),
    allUser: MutableList<User> = mutableListOf(),
) {
    var isLoggedIn = false
    coEvery { isUserLoggedIn() }.answers { Resource.Success(isLoggedIn) }
    coEvery { registerUser(any(), any()) }.answers {
        val email = firstArg<String>()
        val pw = secondArg<String>()
        val userExists = allUser.firstOrNull { it.email == email } != null
        if (userExists) "User exists".asResourceError()
        else Resource.Success<Unit>().also {
            allUser.add(buildUser(email = email))
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
        val users = allUser.filter { u -> uuids.contains(u.uuid) }
        flowOf(Resource.Success(users))
    }
    coEvery { getUserByEmail(any()) }.answers {
        val email = firstArg<String>()
        val users = allUser.firstOrNull { u -> u.email == email }
        Resource.Success(users)
    }
    coEvery { saveUser(any()) }.answers {
        val saveUser = firstArg<User>()
        allUser.replaceAll { u -> if (u.email == saveUser.email) saveUser else u }
        Resource.Success()
    }
}

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