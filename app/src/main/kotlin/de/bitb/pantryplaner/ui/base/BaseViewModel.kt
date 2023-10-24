package de.bitb.pantryplaner.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.bitb.pantryplaner.ui.base.comps.ResString

abstract class BaseViewModel : ViewModel() {

    lateinit var updateWidgets: () -> Unit  //TODO deprecated or fix it
    lateinit var navigate: (Int) -> Unit
    lateinit var navigateBack: () -> Unit
    lateinit var navigateBackTo: (Int) -> Unit
    open fun isBackable(): Boolean = true

    private val _snackbarMessage = MutableLiveData<ResString?>(null)
    val snackbarMessage: LiveData<ResString?> get() = _snackbarMessage
    fun showSnackbar(message: ResString) {
        _snackbarMessage.value = message
    }

    fun clearSnackBar() {
        _snackbarMessage.value = null
    }

}