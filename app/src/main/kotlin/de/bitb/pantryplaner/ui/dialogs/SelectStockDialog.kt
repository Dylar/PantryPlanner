package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.ui.base.testTags.StockTag
import de.bitb.pantryplaner.ui.base.testTags.SelectStockDialogTag
import de.bitb.pantryplaner.ui.base.testTags.testTag

@Composable
fun useSelectStockDialog(
    showDialog: MutableState<Boolean>,
    stocks: List<Stock>,
    onSelect: (Stock) -> Unit,
) {
    if (showDialog.value) {
        SelectStockDialog(
            stocks = stocks,
            onSelect = { check ->
                onSelect(check)
                showDialog.value = false
            },
            onDismiss = { showDialog.value = false },
        )
    }
}

@Composable
private fun SelectStockDialog(
    stocks: List<Stock>,
    onSelect: (Stock) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.testTag(SelectStockDialogTag.DialogTag),
        onDismissRequest = onDismiss,
        text = {
            Column {
                Text("Stocke auswählen")
                stocks.forEach { stock ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stock.name,
                        modifier = Modifier
                            .testTag(StockTag(stock.name))
                            .defaultMinSize(minHeight = 48.dp)
                            .padding(12.dp)
                            .clickable { onSelect(stock) },
                        fontSize = 16.sp,
                    )
                }
            }
        },
        confirmButton = { // TODO new stock button
//            Button(
//                modifier = Modifier.testTag(AddEditStockDialogTag.ConfirmButton),
//                onClick = { onConfirm(copyStock(), true) },
//                content = { Text(confirmButton) }
//            )
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                content = { Text("Abbrechen") }
            )
        }
    )
}
