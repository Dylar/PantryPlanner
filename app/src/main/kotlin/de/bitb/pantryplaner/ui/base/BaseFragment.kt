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
import de.bitb.pantryplaner.ui.base.comps.ResString
import de.bitb.pantryplaner.ui.base.styles.PantryAppTheme
import kotlinx.coroutines.launch

abstract class BaseFragment<T : BaseViewModel> : Fragment() {

    val navController by lazy { NavHostFragment.findNavController(this) }
    lateinit var scaffoldState: ScaffoldState
    abstract val viewModel: T

    private fun settingsFlow() = (activity as MainActivity).settingsFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                var settingsResp by remember { mutableStateOf<Resource<Settings>?>(null) }
                LaunchedEffect(Unit) { settingsFlow().collect { settingsResp = it } }

                when (settingsResp) {
                    is Resource.Error<*> -> ErrorScreen(errorText = settingsResp!!.message!!.asString())
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
        observeNavigateEvent()
        observeSnackBarEvent()
    }

    @Composable
    abstract fun screenContent()

    private fun observeNavigateEvent() {
        viewModel.navigationEvents.observe(viewLifecycleOwner) { event ->
            when (event) {
                NavigateEvent.NavigateBack -> navController.popBackStack()
                is NavigateEvent.Navigate -> navController.navigate(event.route)
                is NavigateEvent.NavigateTo -> navController.popBackStack(event.route, false)
            }
        }
    }

    private fun observeSnackBarEvent() {
        viewModel.snackBarEvents.observe(viewLifecycleOwner) { showSnackBar(it) }
    }

    fun showSnackBar(msg: ResString) {
        lifecycleScope.launch {
            if (::scaffoldState.isInitialized)
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