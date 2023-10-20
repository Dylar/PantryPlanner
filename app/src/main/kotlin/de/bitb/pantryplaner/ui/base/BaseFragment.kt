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
        viewModel.navigate = { navController.navigate(it) } //TODO dont use callbacks
        viewModel.navigateBack = { navController.popBackStack() }
        viewModel.navigateBackTo = { id -> navController.popBackStack(id, false) }
        viewModel.updateWidgets = ::updateWidgets
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                scaffoldState = rememberScaffoldState()
                PantryAppTheme(
                    useDarkTheme = isSystemInDarkTheme()
                ) { screenContent() }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeSnackBarEvent()
    }

    @Composable
    abstract fun screenContent()

    private fun observeSnackBarEvent() {
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message ->
            message?.let { showSnackBar(it) }
        }
    }

    fun showSnackBar(msg: ResString) {
        lifecycleScope.launch {
            if (::scaffoldState.isInitialized)
                scaffoldState.snackbarHostState.showSnackbar(
                    message = msg.asString(resources::getString),
//                    actionLabel = "Do something"
                )
            viewModel.clearSnackBar()
        }
    }

    private fun updateWidgets() {
        val intent = Intent("android.appwidget.action.APPWIDGET_UPDATE")
        requireContext().sendBroadcast(intent)
    }
}