package de.bitb.pantryplaner.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.TestTags
import de.bitb.pantryplaner.ui.base.comps.ErrorScreen
import de.bitb.pantryplaner.ui.base.comps.LoadingIndicator
import de.bitb.pantryplaner.ui.base.comps.asResString
import de.bitb.pantryplaner.ui.base.naviSettingsToReleaseNotes
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog
import de.bitb.pantryplaner.ui.dialogs.InfoDialog

data class PreferenceItem(val title: String, val subtitle: String)

@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsViewModel>() {

    override val viewModel: SettingsViewModel by viewModels()

    @Composable
    override fun screenContent() {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar() },
            content = { buildContent(it) },
        )
    }

    @Composable
    private fun buildAppBar() {
        val showInfoDialog = remember { mutableStateOf(false) }
        if (showInfoDialog.value) {
            InfoDialog(naviToReleaseNotes = ::naviSettingsToReleaseNotes) {
                showInfoDialog.value = false
            }
        }

        TopAppBar(
            modifier = Modifier.testTag(TestTags.SettingsPage.AppBar.name),
            title = { Text(getString(R.string.overview_title)) },
            actions = {
                IconButton(
                    onClick = { showInfoDialog.value = true },
                    modifier = Modifier.testTag(TestTags.SettingsPage.InfoButton.name)
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
        val settings by viewModel.settings.collectAsState(null)
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
//            PreferenceSwitch(// TODO
//                PreferenceItem("DarkMode", "Ist der DarkMode aktiviert?"),
//                checked = settings.isDarkMode,
//                onChange = {
//                    viewModel.saveSettings(settings.copy(isDarkMode = it))
//                },
//            )
            PreferenceSwitch(
                PreferenceItem("Bestand aktualisieren", "Benachrichtigung aktivieren?"),
                checked = settings.refreshAlert,
                onChange = { viewModel.saveSettings(settings.copy(refreshAlert = it)) },
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = { showLogoutDialog.value = true },
                content = { Text("Abmelden") }
            )
        }
    }

}
