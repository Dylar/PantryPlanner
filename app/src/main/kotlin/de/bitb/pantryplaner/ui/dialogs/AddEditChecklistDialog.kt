package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.comps.buildStockDropDown
import de.bitb.pantryplaner.ui.base.comps.buildUserDropDown
import de.bitb.pantryplaner.ui.base.testTags.AddEditChecklistDialogTag
import de.bitb.pantryplaner.ui.base.testTags.testTag

@Composable
fun useAddChecklistDialog(
    showDialog: MutableState<Boolean>,
    users: List<User>,
    stocks: List<Stock>,
    onEdit: (Checklist, Boolean) -> Unit,
) {
    useDialog(
        showDialog,
        "Checklist erstellen", "Hinzufügen",
        users,
        stocks,
        Checklist(),
        onEdit
    )
}

@Composable
fun useEditChecklistDialog(
    showDialog: MutableState<Boolean>,
    users: List<User>,
    stocks: List<Stock>,
    checklist: Checklist,
    onEdit: (Checklist, Boolean) -> Unit,
) {
    useDialog(
        showDialog,
        "Checklist bearbeiten", "Speichern",
        users,
        stocks,
        checklist,
    ) { checklistX, _ -> onEdit(checklistX, true) }
}


@Composable
private fun useDialog(
    showDialog: MutableState<Boolean>,
    title: String,
    confirmButton: String,
    users: List<User>,
    stocks: List<Stock>,
    checklist: Checklist,
    onConfirm: (Checklist, Boolean) -> Unit,
) {
    if (showDialog.value) {
        AddEditChecklistDialog(
            title = title,
            confirmButton = confirmButton,
            checklist = checklist,
            users = users,
            stocks = stocks,
            onConfirm = { loc, close ->
                onConfirm(loc, close)
                showDialog.value = false
            },
            onDismiss = { showDialog.value = false },
        )
    }
}

@Composable
private fun AddEditChecklistDialog(
    title: String,
    confirmButton: String,
    checklist: Checklist,
    users: List<User>,
    stocks: List<Stock>,
    onConfirm: (Checklist, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
//    val isStarted = remember { mutableStateOf(true) }
//    val focusRequester = remember { FocusRequester() }

    var name by remember {
        mutableStateOf(
            TextFieldValue(
                text = checklist.name,
                selection = TextRange(checklist.name.length)
            )
        )
    }

    val selectedStock = remember {
        mutableStateOf(stocks.firstOrNull { it.uuid == checklist.stock } ?: stocks.first())
    }
    val selectedUser = remember {
        val selected = users.filter { checklist.sharedWith.contains(it.uuid) }
        mutableStateOf(selected)
    }

    fun copyChecklist() = checklist.copy(
        name = name.text,
        stock = selectedStock.value.uuid,
        sharedWith = selectedUser.value.map { it.uuid }.toList(),
    )

    AlertDialog(
        modifier = Modifier.testTag(AddEditChecklistDialogTag.DialogTag),
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier
                        .testTag(AddEditChecklistDialogTag.NameLabel)
//                        .focusRequester(focusRequester)
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    label = { Text(stringResource(R.string.item_name)) },
                    value = name,
                    onValueChange = { name = it },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onConfirm(copyChecklist(), false)
                            name = TextFieldValue()
                        },
                    ),
                )
                buildStockDropDown(selectedStock, stocks)
                buildUserDropDown("Checkliste wird nicht geteilt", users, selectedUser)
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
                modifier = Modifier.testTag(AddEditChecklistDialogTag.ConfirmButton),
                onClick = { onConfirm(copyChecklist(), true) },
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
