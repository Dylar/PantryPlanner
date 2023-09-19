package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.comps.buildUserDropDown

@Composable
fun AddChecklistDialog(
    users: List<User>,
    onConfirm: (String, List<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    val selectedUser = remember { mutableStateOf(emptyList<User>()) }
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Checklist anlegen") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    label = { Text(stringResource(R.string.item_name)) },
                    value = name,
                    onValueChange = { name = it },
                    keyboardActions = KeyboardActions(
                        onDone = { onConfirm(name, selectedUser.value.map { it.uuid }) }
                    ),
                )
                buildUserDropDown("Checkliste wird nicht geteilt", users, selectedUser)
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, selectedUser.value.map { it.uuid }) },
                content = { Text("ANLEGEN") }
            )
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                content = { Text("ABBRECHEN") }
            )
        }
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}