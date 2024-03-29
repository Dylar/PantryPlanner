package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.bitb.pantryplaner.BuildConfig

@Composable
fun InfoDialog(naviToReleaseNotes: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Info") },
        text = { InfoContent() },
        confirmButton = {
            Button(
                onClick = naviToReleaseNotes,
                content = { Text("ReleaseNotes") }
            )
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                content = { Text("CLOSE") }
            )
        }
    )
}

@Composable
private fun InfoContent() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "VersionName: ${BuildConfig.VERSION_NAME}")
        Text(text = "Env: ${BuildConfig.FLAVOR}")
    }
}
