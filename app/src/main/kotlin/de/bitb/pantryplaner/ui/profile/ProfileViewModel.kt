package de.bitb.pantryplaner.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    userRepo: UserRepository,
) : BaseViewModel() {

    //TODO make anders
    val user: LiveData<User> = flow { emit(userRepo.getUser().data!!) }.asLiveData()

}

