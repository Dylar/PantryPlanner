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
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.composable.CircleRow
import de.bitb.pantryplaner.ui.base.styles.BaseColors.SelectableColors

@Composable
fun EditDialog(
    item: Item,
    onConfirm: (String, Color) -> Unit,
    onDismiss: () -> Unit
) {
    val color = remember { mutableStateOf(item.color) }
    var category by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit item") },
        text = {
            Column(

            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    label = { Text(stringResource(R.string.item_category)) },
                    value = category,
                    onValueChange = { category = it },
                    keyboardActions = KeyboardActions(
                        onDone = { onConfirm(category, color.value) }
                    ),
                )
                CircleRow(selectedCircleIndex = color, selectableColors = SelectableColors)
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(category, color.value) },
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