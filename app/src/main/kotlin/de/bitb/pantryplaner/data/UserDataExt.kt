package de.bitb.pantryplaner.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.data.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat

interface UserDataExt {

    val userRepo: UserRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getConnectedUsers(): LiveData<Result<List<User>>> {
        return userRepo.getUser()
            .flatMapConcat { resp ->
                if (resp is Result.Error) {
                    return@flatMapConcat MutableStateFlow(resp.castTo())
                }
                userRepo.getUser(resp.data!!.connectedUser)
            }.asLiveData()
    }
}