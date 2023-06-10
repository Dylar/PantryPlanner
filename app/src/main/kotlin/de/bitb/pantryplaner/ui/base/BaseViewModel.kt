package de.bitb.pantryplaner.ui.base

import androidx.lifecycle.ViewModel
import de.bitb.pantryplaner.ui.base.composable.ResString

abstract class BaseViewModel : ViewModel() {
    lateinit var updateWidgets:()-> Unit
    lateinit var navigate: (Int) -> Unit
    lateinit var navigateBack: (Int?) -> Unit
    open lateinit var showSnackbar: (ResString) -> Unit
}