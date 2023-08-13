package de.bitb.pantryplaner.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    userRepo: UserRepository,
) : BaseViewModel() {

    //TODO make anders
    val user: LiveData<Resource<User>> = userRepo.getUser().asLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    val connectedUser: LiveData<Resource<List<User>>> =
        userRepo.getUser()
            .flatMapLatest {
                if (it is Resource.Error) MutableStateFlow(it.castTo())
                else userRepo.getUser(it.data!!.connectedUser)
            }.asLiveData()

}

