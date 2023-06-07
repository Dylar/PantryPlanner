package de.bitb.pantryplaner.ui.check

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.bitb.buttonbuddy.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item") },
        text = {
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                singleLine = true,
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.item_name)) },
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
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
}

