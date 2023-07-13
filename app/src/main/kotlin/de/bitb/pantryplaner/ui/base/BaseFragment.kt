package de.bitb.pantryplaner.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import de.bitb.pantryplaner.ui.base.comps.ResString
import de.bitb.pantryplaner.ui.base.styles.PantryAppTheme
import kotlinx.coroutines.launch

abstract class BaseFragment<T : BaseViewModel> : Fragment() {

    abstract val viewModel: T

    val navController by lazy { NavHostFragment.findNavController(this) }
    lateinit var scaffoldState: ScaffoldState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigate = { navController.navigate(it) }
        viewModel.navigateBack = { id ->
            navController.apply {
                id?.let { popBackStack(id, false) } ?: popBackStack()
            }
        }
        viewModel.showSnackbar = ::showSnackBar
        viewModel.updateWidgets = ::updateWidgets
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            scaffoldState = rememberScaffoldState()
            PantryAppTheme(
                useDarkTheme = isSystemInDarkTheme()
            ) { screenContent() }
        }
    }

    @Composable
    abstract fun screenContent()

    protected fun showSnackBar(msg: ResString) {
        lifecycleScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = msg.asString(resources::getString),
//                    actionLabel = "Do something"
            )
        }
    }

    private fun updateWidgets() {
        val intent = Intent("android.appwidget.action.APPWIDGET_UPDATE")
        requireContext().sendBroadcast(intent)
    }
}