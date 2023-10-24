package de.bitb.pantryplaner.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import de.bitb.pantryplaner.core.MainActivity
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.ui.base.comps.ErrorScreen
import de.bitb.pantryplaner.ui.base.comps.LoadingIndicator
import de.bitb.pantryplaner.ui.base.comps.ResString
import de.bitb.pantryplaner.ui.base.styles.PantryAppTheme
import kotlinx.coroutines.launch

abstract class BaseFragment<T : BaseViewModel> : Fragment() {

    fun settings() = (activity as MainActivity).settings()
    abstract val viewModel: T

    val navController by lazy { NavHostFragment.findNavController(this) }
    lateinit var snackbarHostState: SnackbarHostState

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
                snackbarHostState = remember { SnackbarHostState() }
                var settingsResp by remember { mutableStateOf<Resource<Settings>?>(null) }
                LaunchedEffect(Unit) { settings().collect { settingsResp = it } }

                when (settingsResp) {
                    is Resource.Error<*> -> ErrorScreen(errorText = settingsResp!!.message!!.asString())
                    null -> LoadingIndicator()
                    else -> {
                        val settings = settingsResp?.data
                        val darkMode =
                            (settings == null && isSystemInDarkTheme()) || settings?.darkMode == true
                        PantryAppTheme(useDarkTheme = darkMode) { screenContent() }
                    }
                }
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
            if (::snackbarHostState.isInitialized)
                snackbarHostState.showSnackbar(
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