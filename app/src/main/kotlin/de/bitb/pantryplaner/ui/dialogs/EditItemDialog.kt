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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.composable.CircleRow
import de.bitb.pantryplaner.ui.base.styles.BaseColors.SelectableColors

@Composable
fun EditItemDialog(
    item: Item,
    onConfirm: (String, String, Color) -> Unit,
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
    val color = remember { mutableStateOf(item.color) }
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
                        onDone = { onConfirm(name.text, category, color.value) }
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
                        onDone = { onConfirm(name.text, category, color.value) }
                    ),
                )
                CircleRow(selectedCircleIndex = color, selectableColors = SelectableColors)
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name.text, category, color.value) },
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
