package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.BuildConfig

@Composable
fun InfoDialog(naviToReleaseNotes: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Info")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "VersionName: ${BuildConfig.VERSION_NAME}")
                Text(text = "Env: ${BuildConfig.FLAVOR}")
            }
        },
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
