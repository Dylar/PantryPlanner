package de.bitb.pantryplaner.ui.base

import androidx.lifecycle.ViewModel
import de.bitb.pantryplaner.ui.base.comps.ResString

abstract class BaseViewModel : ViewModel() {
    lateinit var updateWidgets: () -> Unit
    lateinit var navigate: (Int) -> Unit
    lateinit var navigateBack: () -> Unit
    lateinit var navigateBackTo: (Int) -> Unit
    open lateinit var showSnackbar: (ResString) -> Unit
    open fun isBackable(): Boolean = true
}