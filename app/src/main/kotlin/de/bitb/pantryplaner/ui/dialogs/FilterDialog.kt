package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.ui.base.comps.CircleRow
import de.bitb.pantryplaner.ui.base.styles.BaseColors.FilterColors

@Composable
fun FilterDialog(
    filter: Filter,
    selectableColors: List<Color> = FilterColors,
    onConfirm: (Filter) -> Unit,
    onDismiss: () -> Unit,
) {
    val color = remember { mutableStateOf(filter.color) }
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column {
                Text("Info")
                Spacer(modifier = Modifier.height(8.dp))
                CircleRow(selectedCircle = color, selectableColors = selectableColors)
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(filter.copy(color = color.value)) },
                content = { Text("OK") }
            )
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                content = { Text("ABBRECHEN") }
            )
        }
    )
}
