package de.bitb.pantryplaner.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.comps.ErrorScreen
import de.bitb.pantryplaner.ui.base.comps.LoadingIndicator
import de.bitb.pantryplaner.ui.base.naviSettingsToReleaseNotes
import de.bitb.pantryplaner.ui.base.testTags.SettingsPageTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import de.bitb.pantryplaner.ui.comps.buildBottomNavi
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
            bottomBar = {
                buildBottomNavi(
                    checklistsRoute = R.id.settings_to_checklists,
                    recipesRoute = R.id.settings_to_recipes,
                    stocksRoute = R.id.settings_to_stocks,
                    profileRoute = R.id.settings_to_profile,
                )
            },
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
            modifier = Modifier.testTag(SettingsPageTag.AppBar),
            title = { Text(getString(R.string.settings_title)) },
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
            is Result.Error -> ErrorScreen(settings!!.message!!.asString())
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
//            PreferenceSwitch( //TODO fix this page
//                PreferenceItem("Bestand aktualisieren", "Benachrichtigung aktivieren?"),
//                checked = settings.refreshAlert,
//                onChange = { viewModel.saveSettings(settings.copy(refreshAlert = it)) },
//            )
            // TODO import/export DB
            Button(
                modifier = Modifier
                    .testTag(SettingsPageTag.LogoutButton)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                onClick = { showLogoutDialog.value = true },
                content = {
                    Text(
                        "Abmelden",
                        fontSize = 18.sp,
                        letterSpacing = 4.sp
                    )
                }
            )
        }
    }

}
