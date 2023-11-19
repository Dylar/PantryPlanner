package de.bitb.pantryplaner.ui.base

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.IdRes

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
    data class Navigate(@IdRes val route: Int, val args: Bundle? = null) : NaviEvent()
    data class NavigateTo(@IdRes val route: Int, val args: Bundle? = null) : NaviEvent()
    data class NavigateToUrl(val url: String) : NaviEvent()
}
