package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmDialog(title: String, msg: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Text(
                msg,
                modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm() },
                content = { Text("OK") }
            )
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                content = { Text("CANCEL") }
            )
        }
    )
}

