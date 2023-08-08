package de.bitb.pantryplaner.usecase.user

import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.ui.base.comps.ResString
import de.bitb.pantryplaner.ui.base.comps.asResString

sealed class LoginResponse(val message: ResString) {
    class LoggedIn : LoginResponse(R.string.ok.asResString())
    sealed class EmailError(msg: ResString) : LoginResponse(msg) {
        object EmailEmpty : EmailError(R.string.email_is_empty.asResString())
        object EmailInvalidFormat : EmailError(R.string.email_wrong_format.asResString())
    }

    object PwEmpty : LoginResponse(R.string.pw_is_empty.asResString())
    object UserNotFound : LoginResponse(R.string.user_not_found.asResString())
    class ErrorThrown<T>(error: Resource.Error<T>) :
        LoginResponse(error.message ?: ResString.DynamicString("Error thrown"))

    val asError: Resource<LoginResponse>
        get() = Resource.Error(message, this)
}

fun <T> Resource.Error<T>.asError(): Resource<LoginResponse> {
    return Resource.Error(message!!, LoginResponse.ErrorThrown(this))
}

class LoginUC(
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(
        email: String,
        pw: String
    ): Resource<LoginResponse> {
        val isValid = isValid(email, pw)
        if (isValid != null) {
            return isValid.asError
        }

        val loginUserResp = userRepo.loginUser(email, pw)
        if (loginUserResp is Resource.Error) return loginUserResp.asError()

        if (!loginUserResp.hasData) {
            return LoginResponse.UserNotFound.asError
        }

        return Resource.Success(LoginResponse.LoggedIn())
    }

    private fun isValid(email: String, pw: String): LoginResponse? {
        val emailValid = validateEmail(email)
        if (emailValid != null) {
            return emailValid
        }

        if (pw.isBlank()) {
            return LoginResponse.PwEmpty
        }
        return null
    }

    private fun validateEmail(email: String): LoginResponse? {
        if (email.isBlank()) {
            return LoginResponse.EmailError.EmailEmpty
        }

        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        if (!emailRegex.matches(email)) {
            return LoginResponse.EmailError.EmailInvalidFormat
        }

        return null
    }
}