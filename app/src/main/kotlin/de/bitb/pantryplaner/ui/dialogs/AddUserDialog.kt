package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.ui.base.testTags.AddUserDialogTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import kotlin.reflect.KFunction0

@Composable
fun useAddUserDialog(
    showDialog: MutableState<Boolean>,
    onEdit: (String, Boolean) -> Unit,
    onScanOption: KFunction0<Unit>,
) {
    if (showDialog.value) {
        AddUserDialog(
            onScanOption = onScanOption,
            onConfirm = { email, close ->
                onEdit(email, close)
                showDialog.value = false
            },
            onDismiss = { showDialog.value = false },
        )
    }
}

@Composable
private fun AddUserDialog(
    onScanOption: () -> Unit,
    onConfirm: (String, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    val title = "Benutzer verbinden"
    val confirmButton = "Verbinden"

    var email by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        modifier = Modifier.testTag(AddUserDialogTag.DialogTag),
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .testTag(AddUserDialogTag.EmailLabel)
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    label = { Text(stringResource(R.string.user_email)) },
                    value = email,
                    onValueChange = { email = it },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onConfirm(email.text, false)
                            email = TextFieldValue()
                        },
                    ),
                )
                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .size(30.dp)
                        .testTag(AddUserDialogTag.ScanButton),
                    onClick = {
                        onDismiss()
                        onScanOption()
                    },
                    text = { Text(text = "Scannen") },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.QrCodeScanner,
                            contentDescription = "New Stock",
                        )
                    },
                )
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.testTag(AddUserDialogTag.ConfirmButton),
                onClick = { onConfirm(email.text, true) },
                content = { Text(confirmButton) }
            )
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                content = { Text("Abbrechen") }
            )
        }
    )
}
