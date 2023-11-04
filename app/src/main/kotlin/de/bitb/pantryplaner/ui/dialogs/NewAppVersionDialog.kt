package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.ui.base.testTags.NewAppVersionDialogTag
import de.bitb.pantryplaner.ui.base.testTags.testTag

@Composable
fun NewAppVersionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.testTag(NewAppVersionDialogTag.DialogTag),
        onDismissRequest = onDismiss,
        text = {
            Column {
                Text("Neue Version")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Es gibt eine neue Version",
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.testTag(NewAppVersionDialogTag.ConfirmButton),
                onClick = { onConfirm() },
                content = { Text("DOWNLOAD") }
            )
        },
        dismissButton = {
            Button(
                modifier = Modifier.testTag(NewAppVersionDialogTag.CancelButton),
                onClick = onDismiss,
                content = { Text("ABBRECHEN") }
            )
        }
    )
}

