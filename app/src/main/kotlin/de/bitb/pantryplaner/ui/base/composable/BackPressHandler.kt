package de.bitb.pantryplaner.ui.base.composable

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import de.bitb.pantryplaner.ui.base.BaseFragment

@Composable
fun BaseFragment<*>.onBack(exitDialog: @Composable (() -> Unit) -> Unit) {
    val showExitDialog = remember { mutableStateOf(false) }
    BackHandler {
        if (viewModel.isBackable()) {
            navController.popBackStack()
        } else {
            showExitDialog.value = !showExitDialog.value
        }
    }
    if (showExitDialog.value) {
        exitDialog {
            showExitDialog.value = false
        }
    }
}