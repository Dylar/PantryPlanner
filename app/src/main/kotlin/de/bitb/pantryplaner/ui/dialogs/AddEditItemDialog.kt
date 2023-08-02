package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.comps.AddSubRow
import java.util.*

@Composable
fun useAddItemDialog(
    showDialog: MutableState<Boolean>,
    categorys: List<String>,
    onEdit: (Item, Boolean) -> Unit
) {
    useDialog(
        showDialog,
        "Item erstellen", "Hinzufügen",
        Item(), categorys,
        onEdit
    )
}

@Composable
fun useEditItemDialog(
    showDialog: MutableState<Boolean>,
    item: Item,
    categorys: List<String>,
    onEdit: (Item, Boolean) -> Unit
) {
    useDialog(
        showDialog,
        "Item bearbeiten", "Speichern",
        item, categorys
    ) { edited, _ -> onEdit(edited, true) }
}


@Composable
private fun useDialog(
    showDialog: MutableState<Boolean>,
    title: String,
    confirmButton: String,
    item: Item,
    categorys: List<String>,
    onConfirm: (Item, Boolean) -> Unit
) {
    if (showDialog.value) {
        AddEditItemDialog(
            title = title,
            confirmButton = confirmButton,
            item = item,
            categorys = categorys,
            onConfirm = { it, close ->
                onConfirm(it, close)
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
    onConfirm: (Item, Boolean) -> Unit,
    onDismiss: () -> Unit
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
    val freshUntil = remember { mutableStateOf(item.freshUntil) }
    val remindAfter = remember { mutableStateOf(item.remindAfter) }
    val focusRequester = remember { FocusRequester() }

    fun copyItem() = item.copy(
        name = name.text,
        category = category.value.text,
        freshUntil = freshUntil.value,
        remindAfter = remindAfter.value,
    )

    AlertDialog(
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
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                        .focusRequester(focusRequester),
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
                buildCategoryDropDown(category, categorys) { cat ->
                    category.value = TextFieldValue(cat, selection = TextRange(cat.length))
                }
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
        },
        confirmButton = {
            Button(
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
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun OutlinedComp(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .border(width = 2.dp, color = BaseColors.ZergPurple, shape = RoundedCornerShape(4.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) { content() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun buildCategoryDropDown(
    category: MutableState<TextFieldValue>,
    categorys: List<String>,
    onConfirm: (String) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = category.value,
            onValueChange = { category.value = it },
            modifier = Modifier
                .menuAnchor()
                .padding(top = 32.dp, start = 16.dp, end = 16.dp),
            label = { Text(stringResource(R.string.item_category)) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { onConfirm(category.value.text) }),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val addText = "\"${category.value.text}\" wird als neue Kategorie angelegt"
            val input = category.value.text.lowercase(Locale.ROOT)
            val cats = categorys
                .filter {
                    if (it.isBlank()) false
                    else input.isBlank() || it.lowercase(Locale.ROOT).contains(input)
                }
                .sortedBy { !it.lowercase(Locale.ROOT).startsWith(input) }
                .toMutableList()

            if (input.isNotBlank() && cats.none { it.lowercase(Locale.ROOT) == input }) {
                cats.add(0, addText)
            }

            cats.forEach { selectedOption ->
                val isAddText = selectedOption == addText
                DropdownMenuItem(
                    modifier = Modifier
                        .padding(2.dp)
                        .background(BaseColors.LightGray.copy(alpha = .5f)),
                    onClick = {
                        if (!isAddText) {
                            category.value = TextFieldValue(
                                selectedOption,
                                TextRange(selectedOption.length)
                            )
                        }
                        expanded = false
                    },
                    text = {
                        Text(
                            text = selectedOption,
                            color = BaseColors.Black.copy(alpha = if (isAddText) .5f else 1f),
                        )
                    },
                )
            }
        }
    }
}
