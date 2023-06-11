package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import de.bitb.pantryplaner.ui.base.composable.CircleRow

@Composable
fun ColorPickerDialog(
    color: MutableState<Color>,
    onConfirm: (Color) -> Unit,
    onDismiss: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Color") },
        text = { CircleRow(selectedCircleIndex = color) },
        confirmButton = {
            Button(
                onClick = { onConfirm(color.value) },
                content = { Text("SELECT") }
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
