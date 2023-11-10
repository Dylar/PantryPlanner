package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
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
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.comps.buildCategoryDropDown
import de.bitb.pantryplaner.ui.base.comps.buildUserDropDown
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.base.testTags.AddEditItemDialogTag
import de.bitb.pantryplaner.ui.base.testTags.testTag

@Composable
fun useAddItemDialog(
    showDialog: MutableState<Boolean>,
    categorys: List<String>,
    users: List<User>,
    onEdit: (Item, Boolean) -> Unit,
) {
    val item = Item()
    useDialog(
        showDialog,
        "Item erstellen", "Hinzufügen",
        item,
        categorys,
        users,
        users.map { it.uuid },
        true,
        onEdit
    )
}

@Composable
fun useEditItemDialog(
    showDialog: MutableState<Boolean>,
    item: Item,
    categorys: List<String>,
    users: List<User>,
    user: User,
    onEdit: (Item, Boolean) -> Unit,
) {
    val isCreator = user.uuid == item.creator
    useDialog(
        showDialog,
        "Item bearbeiten", "Speichern",
        item,
        categorys,
        users,
        item.sharedWith,
        isCreator,
    ) { i, _ -> onEdit(i, true) }
}

@Composable
private fun useDialog(
    showDialog: MutableState<Boolean>,
    title: String,
    confirmButton: String,
    item: Item,
    categorys: List<String>,
    users: List<User>,
    sharedWith: List<String>,
    isCreator: Boolean,
    onConfirm: (Item, Boolean) -> Unit,
) {
    if (showDialog.value) {
        AddEditItemDialog(
            title = title,
            confirmButton = confirmButton,
            item = item,
            categorys = categorys,
            users = users,
            sharedWith = sharedWith,
            isCreator = isCreator,
            onConfirm = { i, close ->
                onConfirm(i, close)
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
    item: Item,
    categorys: List<String>,
    users: List<User>,
    sharedWith: List<String>,
    isCreator: Boolean,
    onConfirm: (Item, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
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
        val selected = users.filter { sharedWith.contains(it.uuid) }
        mutableStateOf(selected)
    }
//    val freshUntil = remember { mutableLongStateOf(stockItem.freshUntil) }
//    val remindAfter = remember { mutableLongStateOf(stockItem.remindAfter) }

    fun copyItem() = item.copy(
        name = name.text,
        category = category.value.text,
        sharedWith = selectedUser.value.map { it.uuid }.toList(),
    )

    AlertDialog(
        modifier = Modifier.testTag(AddEditItemDialogTag.DialogTag),
        onDismissRequest = onDismiss,
        text = {
            Column {
                Text(title)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    readOnly = !isCreator,
                    modifier = Modifier
                        .testTag(AddEditItemDialogTag.NameLabel)
                        .padding(4.dp)
                        .fillMaxWidth(),
                    singleLine = true,
                    label = { Text(stringResource(R.string.item_name)) },
                    value = name,
                    onValueChange = { name = it },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onConfirm(copyItem(), false)
                            name = TextFieldValue()
                        },
                    ),
                )
                buildCategoryDropDown(category, categorys, canChange = isCreator)
                buildUserDropDown("Item wird nicht geteilt", users, selectedUser)
//                OutlinedComp { //TODO fix this page
//                    Text("MHD", modifier = Modifier.padding(4.dp))
//                    AddSubRow(freshUntil.longValue.toDouble()) {
//                        freshUntil.longValue = it.toLong()
//                    }
//                }
//                OutlinedComp {
//                    Text("Erinnerung", modifier = Modifier.padding(4.dp))
//                    AddSubRow(remindAfter.longValue.toDouble()) {
//                        remindAfter.longValue = it.toLong()
//                    }
//                }
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.testTag(AddEditItemDialogTag.ConfirmButton),
                onClick = { onConfirm(copyItem(), true) },
                content = { Text(confirmButton) }
            )
        },
        dismissButton = {
            Button(
                modifier = Modifier.testTag(AddEditItemDialogTag.CancelButton),
                onClick = { onDismiss() },
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
