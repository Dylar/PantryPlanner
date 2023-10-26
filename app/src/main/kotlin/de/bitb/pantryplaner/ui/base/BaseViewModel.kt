package de.bitb.pantryplaner.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.bitb.pantryplaner.ui.base.comps.ResString

abstract class BaseViewModel : ViewModel() {

    lateinit var updateWidgets: () -> Unit  //TODO deprecated or fix it
    open fun isBackable(): Boolean = true

    private val _navigation = MutableLiveData<NavigateEvent>()
    private val _snackbarMessage = MutableLiveData<ResString>()
    val navigationEvents: LiveData<NavigateEvent> get() = _navigation
    val snackBarEvents: LiveData<ResString> get() = _snackbarMessage

    fun navigate(event: NavigateEvent) {
        _navigation.value = event
    }

    fun showSnackBar(message: ResString) {
        _snackbarMessage.value = message
    }

}