package de.bitb.pantryplaner.ui.intro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.NavigateEvent
import de.bitb.pantryplaner.usecase.UserUseCases
import de.bitb.pantryplaner.usecase.user.LoginResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
) : BaseViewModel() {

    var email by mutableStateOf("")
    var pw by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<LoginResponse?>(null)

    fun login() {
        error = null
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            val result = userUseCases.loginUC(email, pw)
            if (result is Result.Success) navigate(NavigateEvent.Navigate(R.id.login_to_overview))
            else error = result.data
            isLoading = false
        }
    }
}



