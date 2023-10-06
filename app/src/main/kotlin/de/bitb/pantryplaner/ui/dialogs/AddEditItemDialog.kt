package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.StockItem
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.comps.buildCategoryDropDown
import de.bitb.pantryplaner.ui.base.comps.buildUserDropDown
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.base.testTags.AddEditItemDialogTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import de.bitb.pantryplaner.ui.comps.AddSubRow

@Composable
fun useAddItemDialog(
    showDialog: MutableState<Boolean>,
    categorys: List<String>,
    users: List<User>,
    onEdit: (StockItem, Item, Boolean) -> Unit,
) {
    val item = Item()
    useDialog(
        showDialog,
        "Item erstellen", "Hinzufügen",
        item.toStockItem(),
        item,
        categorys,
        users,
        onEdit
    )
}

@Composable
fun useEditItemDialog(
    showDialog: MutableState<Boolean>,
    stockItem: StockItem,
    item: Item,
    categorys: List<String>,
    users: List<User>,
    onEdit: (StockItem, Item, Boolean) -> Unit,
) {
    useDialog(
        showDialog,
        "Item bearbeiten", "Speichern",
        stockItem,
        item,
        categorys,
        users,
    ) { si, i, _ -> onEdit(si, i, true) }
}

@Composable
private fun useDialog(
    showDialog: MutableState<Boolean>,
    title: String,
    confirmButton: String,
    stockItem: StockItem,
    item: Item,
    categorys: List<String>,
    users: List<User>,
    onConfirm: (StockItem, Item, Boolean) -> Unit,
) {
    if (showDialog.value) {
        AddEditItemDialog(
            title = title,
            confirmButton = confirmButton,
            stockItem = stockItem,
            item = item,
            categorys = categorys,
            users = users,
            onConfirm = { si, i, close ->
                onConfirm(si, i, close)
                showDialog.value = false
            },
            onDismiss = { showDialog.value = false },
        )
    }
}

@Composable
private fun AddEditItemDialog(
    title: String,
    confirmButton: String,
    stockItem: StockItem,
    item: Item,
    categorys: List<String>,
    users: List<User>,
    onConfirm: (StockItem, Item, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
//    val isStarted = remember { mutableStateOf(true) }
//    val focusRequester = remember { FocusRequester() }

    var name by remember {
        mutableStateOf(
            TextFieldValue(
                text = item.name,
                selection = TextRange(item.name.length)
            )
        )
    }

    val category = remember { mutableStateOf(TextFieldValue(item.category)) }
    val selectedUser = remember {
        val selected = users.filter { item.sharedWith.contains(it.uuid) }
        mutableStateOf(selected)
    }
    val freshUntil = remember { mutableStateOf(stockItem.freshUntil) }
    val remindAfter = remember { mutableStateOf(stockItem.remindAfter) }

    fun copyItem() = item.copy(
        name = name.text,
        category = category.value.text,
        sharedWith = selectedUser.value.map { it.uuid }.toList(),
    )

    fun copyStockItem() = stockItem.copy(
        freshUntil = freshUntil.value,
        remindAfter = remindAfter.value,
    )

    AlertDialog(
        modifier = Modifier.testTag(AddEditItemDialogTag.DialogTag),
        onDismissRequest = onDismiss,
//        containerColor = darkColorPalette.background,
//        iconContentColor = darkColorPalette.onSurface,
//        titleContentColor = darkColorPalette.onSurface,
//        textContentColor = darkColorPalette.onSurface,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier
                        .testTag(AddEditItemDialogTag.NameLabel)
//                        .focusRequester(focusRequester)
                        .padding(4.dp)
                        .fillMaxWidth(),
                    singleLine = true,
                    label = { Text(stringResource(R.string.item_name)) },
                    value = name,
                    onValueChange = { name = it },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onConfirm(copyStockItem(), copyItem(), false)
                            name = TextFieldValue()
                        },
                    ),
                )
                buildCategoryDropDown(category, categorys)
                buildUserDropDown("Item wird nicht geteilt", users, selectedUser)
                OutlinedComp {
                    Text("MHD", modifier = Modifier.padding(4.dp))
                    AddSubRow(
                        freshUntil.value.toDouble(),
                        backgroundColor = BaseColors.LightGray
                    ) { freshUntil.value = it.toLong() }
                }
                OutlinedComp {
                    Text("Erinnerung", modifier = Modifier.padding(4.dp))
                    AddSubRow(
                        remindAfter.value.toDouble(),
                        backgroundColor = BaseColors.LightGray
                    ) { remindAfter.value = it.toLong() }
                }
            }
//            LaunchedEffect(Unit) {
//                if (isStarted.value) {
//                    isStarted.value = false
//                    focusRequester.requestFocus()
//                }
//            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.testTag(AddEditItemDialogTag.ConfirmButton),
                onClick = { onConfirm(copyStockItem(), copyItem(), true) },
                content = { Text(confirmButton) }
            )
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                content = { Text("Abbrechen") }
            )
        }
    )
}

@Composable
private fun OutlinedComp(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .padding(top = 4.dp, start = 4.dp, end = 4.dp)
            .border(width = 2.dp, color = BaseColors.ZergPurple, shape = RoundedCornerShape(4.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) { content() }
}
