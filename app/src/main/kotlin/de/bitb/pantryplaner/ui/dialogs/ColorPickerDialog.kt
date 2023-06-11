package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import de.bitb.pantryplaner.ui.base.composable.CircleRow
import de.bitb.pantryplaner.ui.base.styles.BaseColors.FilterColors

@Composable
fun ColorPickerDialog(
    color: MutableState<Color>,
    selectableColors: List<Color> = FilterColors,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Color") },
        text = { CircleRow(selectedCircleIndex = color, selectableColors = selectableColors) },
        confirmButton = {
            Button(
                onClick = { onConfirm() },
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
