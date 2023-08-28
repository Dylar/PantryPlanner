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
import de.bitb.pantryplaner.usecase.user.RegisterResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
) : BaseViewModel() {

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var email by mutableStateOf("")
    var pw1 by mutableStateOf("")
    var pw2 by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<RegisterResponse?>(null)

    fun register() {
        error = null
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            val result = userUseCases.registerUC(firstName, lastName, email, pw1, pw2)
            if (result is Resource.Error) error = result.data
            else navigate(R.id.register_to_overview)
            isLoading = false
        }
    }
}



