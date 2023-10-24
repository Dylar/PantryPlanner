package de.bitb.pantryplaner.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.comps.ErrorScreen
import de.bitb.pantryplaner.ui.base.comps.LoadingIndicator
import de.bitb.pantryplaner.ui.base.naviSettingsToReleaseNotes
import de.bitb.pantryplaner.ui.base.testTags.SettingsPageTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog
import de.bitb.pantryplaner.ui.dialogs.InfoDialog

data class PreferenceItem(val title: String, val subtitle: String)

@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsViewModel>() {

    override val viewModel: SettingsViewModel by viewModels()

    @Composable
    override fun screenContent() {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = { buildAppBar() },
            content = { buildContent(it) },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun buildAppBar() {
        val showInfoDialog = remember { mutableStateOf(false) }
        if (showInfoDialog.value) {
            InfoDialog(naviToReleaseNotes = ::naviSettingsToReleaseNotes) {
                showInfoDialog.value = false
            }
        }

        TopAppBar(
            modifier = Modifier.testTag(SettingsPageTag.AppBar),
            title = { Text(getString(R.string.overview_title)) },
            actions = {
                IconButton(
                    onClick = { showInfoDialog.value = true },
                    modifier = Modifier.testTag(SettingsPageTag.InfoButton)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info dialog button"
                    )
                }
            }
        )
    }

    @Composable
    private fun buildContent(innerPadding: PaddingValues) {
        val settings by viewModel.settings.observeAsState(null)
        when (settings) {
            is Resource.Error -> ErrorScreen(settings!!.message!!.asString())
            null -> LoadingIndicator()
            else -> SettingsPage(innerPadding, settings!!.data!!)
        }
    }

    @Composable
    private fun SettingsPage(padding: PaddingValues, settings: Settings) {
        val showLogoutDialog = remember { mutableStateOf(false) }
        if (showLogoutDialog.value) {
            ConfirmDialog(
                "Abmelden?",
                "Möchten sie sich abmelden?",
                viewModel::logout
            ) {
                showLogoutDialog.value = false
            }
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            PreferenceSwitch(
                PreferenceItem("DarkMode", "Ist der DarkMode aktiviert?"),
                checked = settings.darkMode != false,
                onChange = { viewModel.saveSettings(settings.copy(darkMode = it)) },
            )
            PreferenceSwitch(
                PreferenceItem("Bestand aktualisieren", "Benachrichtigung aktivieren?"),
                checked = settings.refreshAlert,
                onChange = { viewModel.saveSettings(settings.copy(refreshAlert = it)) },
            )
            Button(
                modifier = Modifier
                    .testTag(SettingsPageTag.LogoutButton)
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = { showLogoutDialog.value = true },
                content = { Text("Abmelden") }
            )
        }
    }

}
