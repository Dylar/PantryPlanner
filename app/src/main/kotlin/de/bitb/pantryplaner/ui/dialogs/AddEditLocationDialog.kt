package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.data.model.Location
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.comps.buildUserDropDown
import de.bitb.pantryplaner.ui.base.testTags.AddEditLocationDialogTag
import de.bitb.pantryplaner.ui.base.testTags.testTag

@Composable
fun useAddLocationDialog(
    showDialog: MutableState<Boolean>,
    users: List<User>,
    onEdit: (Location, Boolean) -> Unit,
) {
    useDialog(
        showDialog,
        "Ort erstellen", "Hinzufügen",
        Location(),
        users,
        onEdit
    )
}

@Composable
fun useEditLocationDialog(
    showDialog: MutableState<Boolean>,
    location: Location,
    users: List<User>,
    onEdit: (Location, Boolean) -> Unit,
) {
    useDialog(
        showDialog,
        "Ort bearbeiten", "Speichern",
        location,
        users,
    ) {  loc, _ -> onEdit(loc, true) }
}


@Composable
private fun useDialog(
    showDialog: MutableState<Boolean>,
    title: String,
    confirmButton: String,
    location: Location,
    users: List<User>,
    onConfirm: (Location, Boolean) -> Unit,
) {
    if (showDialog.value) {
        AddEditLocationDialog(
            title = title,
            confirmButton = confirmButton,
            location=location,
            users = users,
            onConfirm = { loc, close ->
                onConfirm(loc, close)
                showDialog.value = false
            },
            onDismiss = { showDialog.value = false },
        )
    }
}

@Composable
private fun AddEditLocationDialog(
    title: String,
    confirmButton: String,
    location: Location,
    users: List<User>,
    onConfirm: (Location, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember {
        mutableStateOf(
            TextFieldValue(
                text = location.name,
                selection = TextRange(location.name.length)
            )
        )
    }

    val selectedUser = remember { mutableStateOf(emptyList<User>()) }
    val focusRequester = remember { FocusRequester() }

    fun copyLocation() = location.copy(
        name = name.text,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
//        containerColor = darkColorPalette.background,
//        iconContentColor = darkColorPalette.onSurface,
//        titleContentColor = darkColorPalette.onSurface,
//        textContentColor = darkColorPalette.onSurface,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier
                        .testTag(AddEditLocationDialogTag.NameLabel)
                        .padding(horizontal = 16.dp)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    label = { Text(stringResource(R.string.item_name)) },
                    value = name,
                    onValueChange = { name = it },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onConfirm( copyLocation(), false)
                            name = TextFieldValue()
                        },
                    ),
                )
                buildUserDropDown("Ort wird nicht geteilt", users, selectedUser)
            }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.testTag(AddEditLocationDialogTag.ConfirmButton),
                onClick = { onConfirm( copyLocation(), true) },
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
