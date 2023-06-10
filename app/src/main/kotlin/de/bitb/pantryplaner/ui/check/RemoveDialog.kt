package de.bitb.pantryplaner.ui.check

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.data.model.Item

@Composable
fun ConfirmDialog(title:String, msg:String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
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

