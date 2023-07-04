package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.data.model.Item

@Composable
fun useEditItemDialog(
    showEditDialog: MutableState<Boolean>,
    item: Item,
    onEdit: (Item, String, String) -> Unit
) {
    if (showEditDialog.value) {
        EditItemDialog(
            item = item,
            onConfirm = { name, category ->
                onEdit(item, name, category)
                showEditDialog.value = false
            },
            onDismiss = { showEditDialog.value = false },
        )
    }
}

@Composable
fun EditItemDialog(
    item: Item,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember {
        mutableStateOf(
            TextFieldValue(
                text = item.name,
                selection = TextRange(item.name.length)
            )
        )
    }
    var category by remember { mutableStateOf(item.category) }
    val focusRequester = remember { FocusRequester() }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit item") },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    label = { Text(stringResource(R.string.item_name)) },
                    value = name,
                    onValueChange = { name = it },
                    keyboardActions = KeyboardActions(
                        onDone = { onConfirm(name.text, category) }
                    ),
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                    singleLine = true,
                    label = { Text(stringResource(R.string.item_category)) },
                    value = category,
                    onValueChange = { category = it },
                    keyboardActions = KeyboardActions(
                        onDone = { onConfirm(name.text, category) }
                    ),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name.text, category) },
                content = { Text("EDIT") }
            )
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                content = { Text("CANCEL") }
            )
        }
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
