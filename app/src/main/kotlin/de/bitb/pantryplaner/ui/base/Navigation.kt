package de.bitb.pantryplaner.ui.base

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.ui.checklists.details.ChecklistViewModel
import de.bitb.pantryplaner.ui.intro.LoginViewModel
import de.bitb.pantryplaner.ui.checklists.ChecklistsViewModel
import de.bitb.pantryplaner.ui.profile.ProfileViewModel
import de.bitb.pantryplaner.ui.settings.SettingsViewModel


fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

fun Activity.navigateToURL(url: String) {
    Intent(Intent.ACTION_VIEW)
        .apply { data = Uri.parse(url) }
        .also(::startActivity)
}

sealed class NaviEvent {
    object NavigateBack : NaviEvent()
    data class Navigate(@IdRes val route: Int) : NaviEvent()
    data class NavigateTo(@IdRes val route: Int) : NaviEvent()
    data class NavigateToUrl(val url: String) : NaviEvent()
}

fun BaseFragment<SettingsViewModel>.naviSettingsToReleaseNotes() {
    navController.navigate(R.id.settings_to_releasenotes)
}

fun BaseFragment<ProfileViewModel>.naviToScan() {
    navController.navigate(R.id.profile_to_scan)
}

fun BaseFragment<LoginViewModel>.naviLoginToReleaseNotes() {
    navController.navigate(R.id.login_to_releasenotes)
}

fun BaseFragment<LoginViewModel>.naviToRegister() {
    navController.navigate(R.id.login_to_register)
}

fun BaseFragment<ChecklistsViewModel>.naviToChecklist(uuid: String) {
    navController
        .navigate(R.id.checklists_to_checklist_details, bundleOf(KEY_CHECKLIST_UUID to uuid))
}

fun BaseFragment<ChecklistViewModel>.naviChecklistToItems(uuid: String) {
    navController
        .navigate(R.id.checklist_details_to_select_items, bundleOf(KEY_CHECKLIST_UUID to uuid))
}


