package de.bitb.pantryplaner.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import de.bitb.pantryplaner.core.misc.Logger
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat

interface UserDataExt {

    val userRepo: UserRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getConnectedUsers(): LiveData<Resource<List<User>>> {
        return userRepo.getUser()
            .flatMapConcat { resp ->
                if (resp is Resource.Error) {
                    return@flatMapConcat MutableStateFlow(resp.castTo())
                }
                Logger.justPrint("connectedUser: ${resp.data!!.connectedUser}")
                userRepo.getUser(resp.data!!.connectedUser)
            }.asLiveData()
    }
}