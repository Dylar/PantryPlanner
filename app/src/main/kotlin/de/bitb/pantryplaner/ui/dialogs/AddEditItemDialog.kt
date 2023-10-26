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
    isCreator: Boolean,
    onConfirm: (Item, Boolean) -> Unit,
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
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    readOnly = !isCreator,
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
                            onConfirm(copyItem(), false)
                            name = TextFieldValue()
                        },
                    ),
                )
                buildCategoryDropDown(category, categorys, canChange = isCreator)
                buildUserDropDown("Item wird nicht geteilt", users, selectedUser)
//                OutlinedComp { //TODO fix me
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
                onClick = { onConfirm(copyItem(), true) },
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
