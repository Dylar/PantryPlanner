package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.ui.base.comps.CircleRow
import de.bitb.pantryplaner.ui.base.styles.BaseColors.FilterColors

@Composable
fun FilterDialog(
    filter: Filter,
    selectableColors: List<Color> = FilterColors,
    onConfirm: (Filter) -> Unit,
    onDismiss: () -> Unit
) {
    val color = remember { mutableStateOf(filter.color) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter") },
        text = { CircleRow(selectedCircle = color, selectableColors = selectableColors) },
        confirmButton = {
            Button(
                onClick = { onConfirm(filter.copy(color = color.value)) },
                content = { Text("OK") }
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
