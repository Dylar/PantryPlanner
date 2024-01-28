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
import de.bitb.pantryplaner.ui.base.testTags.ConfirmDialogTag
import de.bitb.pantryplaner.ui.base.testTags.testTag

@Composable
fun ConfirmDialog(
    title: String,
    msg: String,
    confirmBtn: String = "",
    cancelBtn: String = "",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column {
                Text(title)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    msg,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.testTag(ConfirmDialogTag.ConfirmButton),
                onClick = { onConfirm() },
                content = { Text(confirmBtn.ifBlank { "OK" }) }
            )
        },
        dismissButton = {
            Button(
                modifier = Modifier.testTag(ConfirmDialogTag.DismissButton),
                onClick = { onDismiss() },
                content = { Text(cancelBtn.ifBlank { "ABBRECHEN" }) }
            )
        }
    )
}

