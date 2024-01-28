package de.bitb.pantryplaner.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.bitb.pantryplaner.ui.base.comps.ResString

abstract class BaseViewModel : ViewModel() {

    lateinit var updateWidgets: () -> Unit  //TODO deprecated or fix it
    open fun isBackable(): Boolean = true

    private val _navigation = MutableLiveData<NaviEvent?>()
    private val _snackbarMessage = MutableLiveData<ResString?>()
    val navigationEvents: LiveData<NaviEvent?> get() = _navigation
    val snackBarEvents: LiveData<ResString?> get() = _snackbarMessage

    fun clearNavi() {
        _navigation.value = null
    }

    fun clearSnackBar() {
        _snackbarMessage.value = null
    }

    fun navigate(event: NaviEvent) {
        _navigation.value = event
    }

    fun showSnackBar(message: ResString) {
        _snackbarMessage.value = message
    }

}