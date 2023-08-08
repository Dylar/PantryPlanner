package de.bitb.pantryplaner.ui.intro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.usecase.UserUseCases
import de.bitb.pantryplaner.usecase.user.LoginResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
) : BaseViewModel() {

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<LoginResponse?>(null)

    var email by mutableStateOf("")
    var pw by mutableStateOf("")

    fun login() {
        if (isLoading) {
            return
        }
        error = null
        isLoading = true
        viewModelScope.launch {
            val result = userUseCases.loginUC(email, pw)
            if (result is Resource.Success) {
                navigate(R.id.login_to_overview)
            } else {
                error = result.data
            }
            isLoading = false
        }
    }
}


