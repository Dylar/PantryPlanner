package de.bitb.pantryplaner.ui.base

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import de.bitb.pantryplaner.R

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

fun Fragment.naviToReleaseNotes() {
    NavHostFragment.findNavController(this).navigate(R.id.overview_to_releasenotes)
}

fun Fragment.naviToItems() {
    NavHostFragment.findNavController(this).navigate(R.id.overview_to_items)
}

fun Fragment.naviToChecklist(uuid: String) {
    NavHostFragment.findNavController(this)
        .navigate(R.id.overview_to_checklist, bundleOf(KEY_CHECKLIST_UUID to uuid))
}

