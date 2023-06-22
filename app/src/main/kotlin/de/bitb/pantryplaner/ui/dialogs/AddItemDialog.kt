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
import de.bitb.pantryplaner.ui.base.composable.CircleRow
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun AddItemDialog(onConfirm: (String, String, Color, Boolean) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    val color = remember { MutableStateFlow(BaseColors.SelectableColors.first()) }
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item") },
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
                        onDone = {
                            onConfirm(name, category, color.value, false)
                            name = ""
                        }
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
                        onDone = {
                            onConfirm(name, category, color.value, false)
                            name = ""
                        }
                    ),
                )
                CircleRow(
                    selectedCircleIndex = color,
                    selectableColors = BaseColors.SelectableColors
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, category, color.value, true) },
                content = { Text("ADD") }
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