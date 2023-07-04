package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.comps.CircleRow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

@Composable
fun AddItemDialog(
    categorys: List<String>,
    onConfirm: (String, String, Color, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    val category = remember { mutableStateOf(TextFieldValue("")) }
    val color = remember { MutableStateFlow(Filter(color = BaseColors.SelectableColors.first())) }
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item") },
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
                            onConfirm(name, category.value.text, color.value.color, false)
                            name = ""
                        }
                    ),
                )
                buildCategoryDropDown(category, categorys) { cat ->
                    onConfirm(name, cat, color.value.color, false)
                    name = ""
                }
                CircleRow(
                    selectedCircleIndex = color,
                    selectableColors = BaseColors.SelectableColors
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, category.value.text, color.value.color, true) },
                content = { Text("ADD") }
            )
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                content = { Text("CANCEL") }
            )
        }
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun buildCategoryDropDown(
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
