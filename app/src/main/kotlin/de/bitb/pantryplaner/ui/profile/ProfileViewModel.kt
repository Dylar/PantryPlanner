package de.bitb.pantryplaner.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.LocationRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Location
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.usecase.LocationUseCases
import de.bitb.pantryplaner.usecase.UserUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileModel(
    val user: User?,
    val connectedUser: List<User>?,
    val locations: List<Location>?,
) {
    val isLoading: Boolean
        get() = locations == null || connectedUser == null || user == null
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    userRepo: UserRepository,
    locationRepo: LocationRepository,
    private val userUseCases: UserUseCases,
    private val locationUseCases: LocationUseCases,
) : BaseViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val profileModel: LiveData<Resource<ProfileModel>> =
        userRepo.getUser()
            .flatMapLatest { userResp ->
                if (userResp is Resource.Error)
                    return@flatMapLatest MutableStateFlow(userResp.castTo<ProfileModel>())

                val user = userResp.data!!
                combine(
                    userRepo.getUser(user.connectedUser),
                    locationRepo.getLocations(),
                ) { usersResp, locationResp ->
                    when {
                        usersResp is Resource.Error -> usersResp.castTo()
                        locationResp is Resource.Error -> locationResp.castTo()
                        else -> Resource.Success(
                            ProfileModel(
                                user,
                                usersResp.data,
                                locationResp.data
                            )
                        )
                    }
                }
            }.asLiveData()

    fun disconnectUser(user: User) {
        viewModelScope.launch {
            when (val resp = userUseCases.disconnectUserUC(user)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> showSnackbar("Benutzer entfernt: ${user.fullName}".asResString())
            }
        }
    }

    fun addLocation(location: Location) {
        viewModelScope.launch {
            when (val resp = locationUseCases.addLocationUC(location)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                else -> showSnackbar("Ort hinzugefügt: ${location.name}".asResString())
            }
        }
    }

    fun disconnectLocation(location: Location) {
        // TODO
//        viewModelScope.launch {
//            when (val resp = locationUseCases.disconnectLocationUC(location)) {
//                is Resource.Error -> showSnackbar(resp.message!!)
//                else -> showSnackbar("Ort entfernt: ${location.name}".asResString())
//            }
//        }
    }

}

