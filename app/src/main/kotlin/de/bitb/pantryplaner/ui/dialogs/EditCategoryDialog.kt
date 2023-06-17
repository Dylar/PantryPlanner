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

@Composable
fun EditCategoryDialog(
    category: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var categoryState by remember {
        mutableStateOf(
            TextFieldValue(
                text = category,
                selection = TextRange(category.length)
            )
        )
    }
    val focusRequester = remember { FocusRequester() }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit category") },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    label = { Text(stringResource(R.string.item_category)) },
                    value = categoryState,
                    onValueChange = { categoryState = it },
                    keyboardActions = KeyboardActions(
                        onDone = { onConfirm(categoryState.text) }
                    ),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(categoryState.text) },
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
