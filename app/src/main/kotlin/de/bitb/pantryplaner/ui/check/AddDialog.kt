package de.bitb.pantryplaner.ui.check

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddDialog(onConfirm: (String, Boolean) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item") },
        text = {
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                    .focusRequester(focusRequester),
                singleLine = true,
                label = { Text(stringResource(R.string.item_name)) },
                value = name,
                onValueChange = { name = it },
                keyboardActions = KeyboardActions(onDone = {
                    onConfirm(name, false)
                    name = ""
                }),
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, true) },
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

