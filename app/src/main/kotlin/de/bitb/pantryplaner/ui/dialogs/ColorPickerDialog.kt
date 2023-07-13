package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.ui.base.comps.CircleRow
import de.bitb.pantryplaner.ui.base.styles.BaseColors.FilterColors
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun FilterDialog(
    filter: MutableStateFlow<Filter>,
    selectableColors: List<Color> = FilterColors,
    onConfirm: (Filter) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter") },
        text = { CircleRow(selectedCircleIndex = filter, selectableColors = selectableColors) },
        confirmButton = {
            Button(
                onClick = { onConfirm(filter.value) },
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
