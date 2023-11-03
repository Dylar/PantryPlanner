package de.bitb.pantryplaner.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.usecase.StockUseCases
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
    val stocks: List<Stock>?,
) {
    val isLoading: Boolean
        get() = stocks == null || connectedUser == null || user == null
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    userRepo: UserRepository,
    stockRepo: StockRepository,
    private val userUseCases: UserUseCases,
    private val stockUseCases: StockUseCases,
) : BaseViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val profileModel: LiveData<Result<ProfileModel>> =
        userRepo.getUser()
            .flatMapLatest { userResp ->
                if (userResp is Result.Error)
                    return@flatMapLatest MutableStateFlow(userResp.castTo<ProfileModel>())

                val user = userResp.data!!
                combine(
                    userRepo.getUser(user.connectedUser),
                    stockRepo.getStocks(),
                ) { usersResp, StockResp ->
                    when {
                        usersResp is Result.Error -> usersResp.castTo()
                        StockResp is Result.Error -> StockResp.castTo()
                        else -> Result.Success(
                            ProfileModel(
                                user,
                                usersResp.data,
                                StockResp.data
                            )
                        )
                    }
                }
            }.asLiveData(viewModelScope.coroutineContext)

    fun disconnectUser(user: User) {
        viewModelScope.launch {
            when (val resp = userUseCases.disconnectUserUC(user)) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> showSnackBar("Benutzer entfernt: ${user.fullName}".asResString())
            }
        }
    }

    fun addStock(stock: Stock) {
        viewModelScope.launch {
            when (val resp = stockUseCases.addStockUC(stock)) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> showSnackBar("Lager hinzugefügt: ${stock.name}".asResString())
            }
        }
    }

    fun removeStock(stock: Stock) {
        viewModelScope.launch {
            when (val resp = stockUseCases.deleteStockUC(stock)) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> showSnackBar("Lager entfernt: ${stock.name}".asResString())
            }
        }
    }

    fun editStock(stock: Stock) {
        viewModelScope.launch {
            when (val resp = stockUseCases.editStockUC(stock)) {
                is Result.Error -> showSnackBar(resp.message!!)
                else -> showSnackBar("Lager editiert: ${stock.name}".asResString())
            }
        }
    }

    fun connectUser(email: String) {
        viewModelScope.launch {
            val res = userUseCases.connectUserUC(email)
            if (res is Result.Error) showSnackBar(res.message!!)
            else showSnackBar("Benutzer hinzugefügt".asResString())
        }
    }

}

