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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.comps.buildUserDropDown
import de.bitb.pantryplaner.ui.base.testTags.AddEditItemDialogTag
import de.bitb.pantryplaner.ui.base.testTags.AddEditStockDialogTag
import de.bitb.pantryplaner.ui.base.testTags.testTag

@Composable
fun useAddStockDialog(
    showDialog: MutableState<Boolean>,
    users: List<User>,
    onEdit: (Stock, Boolean) -> Unit,
) {
    useDialog(
        showDialog,
        "Lager erstellen", "Hinzufügen",
        Stock(),
        users,
        onEdit
    )
}

@Composable
fun useEditStockDialog(
    showDialog: MutableState<Boolean>,
    stock: Stock,
    users: List<User>,
    onEdit: (Stock, Boolean) -> Unit,
) {
    useDialog(
        showDialog,
        "Lager bearbeiten", "Speichern",
        stock,
        users,
    ) { stockX, _ -> onEdit(stockX, true) }
}


@Composable
private fun useDialog(
    showDialog: MutableState<Boolean>,
    title: String,
    confirmButton: String,
    stock: Stock,
    users: List<User>,
    onConfirm: (Stock, Boolean) -> Unit,
) {
    if (showDialog.value) {
        AddEditStockDialog(
            title = title,
            confirmButton = confirmButton,
            stock = stock,
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
private fun AddEditStockDialog(
    title: String,
    confirmButton: String,
    stock: Stock,
    users: List<User>,
    onConfirm: (Stock, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
//    val isStarted = remember { mutableStateOf(true) }
//    val focusRequester = remember { FocusRequester() }

    var name by remember {
        mutableStateOf(
            TextFieldValue(
                text = stock.name,
                selection = TextRange(stock.name.length)
            )
        )
    }

    val selectedUser = remember {
        val selected = users.filter { stock.sharedWith.contains(it.uuid) }
        mutableStateOf(selected)
    }

    fun copyStock() = stock.copy(
        name = name.text,
        sharedWith = selectedUser.value.map { it.uuid }.toList(),
    )

    AlertDialog(
        modifier = Modifier.testTag(AddEditStockDialogTag.DialogTag),
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
                        .testTag(AddEditStockDialogTag.NameLabel)
//                        .focusRequester(focusRequester)
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    label = { Text(stringResource(R.string.item_name)) },
                    value = name,
                    onValueChange = { name = it },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onConfirm(copyStock(), false)
                            name = TextFieldValue()
                        },
                    ),
                )
                buildUserDropDown("Lager wird nicht geteilt", users, selectedUser)
            }

//            LaunchedEffect(Unit) {
//                if (isStarted.value) {
//                    isStarted.value = false
//                    focusRequester.requestFocus()
//                }
//            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.testTag(AddEditStockDialogTag.ConfirmButton),
                onClick = { onConfirm(copyStock(), true) },
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
