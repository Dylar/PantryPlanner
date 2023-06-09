package de.bitb.pantryplaner.ui.check

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.data.model.Item

@Composable
fun RemoveDialog(item: Item, onConfirm: (Item) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Remove Item") },
        text = {
            Text(
                "Möchtest du folgendes Item entfernen?\n${item.name}",
                modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(item) },
                content = { Text("ENTFERNEN") }
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

