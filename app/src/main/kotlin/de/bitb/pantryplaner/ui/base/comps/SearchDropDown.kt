package de.bitb.pantryplaner.ui.base.comps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import java.util.*

@Composable
fun buildCategoryDropDown(category: MutableState<TextFieldValue>, categorys: List<String>) {
    SearchDropDown(
        stringResource(R.string.item_category),
        category.value.text,
        addUnknownOption = true,
        options = categorys,
    ) { cat ->
        category.value = TextFieldValue(cat, selection = TextRange(cat.length))
    }
}

@Composable
fun buildUserDropDown(
    emptyText: String,
    users: List<User>,
    selectedUser: MutableState<List<User>>,
    canChange: Boolean = true,
    onSelect: (List<User>) -> Unit = {},
) {
    if (canChange) {
        SearchDropDown(
            "Mit Benutzer teilen",
            clearOnSelection = true,
            options = users.filter { !selectedUser.value.contains(it) }.map { it.fullName },
        ) { selection ->
            val user = users.firstOrNull { it.fullName == selection }
            if (user != null) {
                val list = selectedUser.value.toMutableList()
                if (!list.remove(user)) {
                    list.add(user)
                }
                selectedUser.value = list
                onSelect(list)
            }
        }
    }
    ConnectedUser(emptyText, selectedUser, canChange, onSelect)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchDropDown(
    hint: String,
    selected: String = "",
    options: List<String>,
    addUnknownOption: Boolean = false,
    clearOnSelection: Boolean = false,
    onConfirm: (String) -> Unit,
) {
    var selectedState by remember {
        mutableStateOf(TextFieldValue(selected, selection = TextRange(selected.length)))
    }
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = Modifier.padding(4.dp).fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedState,
            onValueChange = { selectedState = it },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            label = { Text(hint) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { onConfirm(selectedState.text) }),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            val addText = "\"${selectedState.text}\" wird neu angelegt"
            val input = selectedState.text.lowercase(Locale.ROOT)
            val cats = options
                .filter {
                    if (it.isBlank()) false
                    else input.isBlank() || it.lowercase(Locale.ROOT).contains(input)
                }
                .sortedBy { !it.lowercase(Locale.ROOT).startsWith(input) }
                .toMutableList()

            if (addUnknownOption &&
                input.isNotBlank() &&
                cats.none { it.lowercase(Locale.ROOT) == input }
            ) {
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
                            selectedState =
                                if (clearOnSelection) {
                                    TextFieldValue("")
                                } else {
                                    TextFieldValue(
                                        selectedOption,
                                        TextRange(selectedOption.length)
                                    )
                                }
                            onConfirm(selectedOption)
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

@Composable
private fun ConnectedUser(
    emptyText: String,
    selectedUser: MutableState<List<User>>,
    canChange: Boolean,
    onSelect: (List<User>) -> Unit,
) {
    Card(
        modifier = Modifier
            .defaultMinSize(minHeight = 32.dp)
            .padding(4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BaseColors.DarkGray.copy(alpha = .4f)),
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .defaultMinSize(minHeight = 32.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (selectedUser.value.isEmpty()) {
                item(1) {
                    Text(
                        emptyText,
                        modifier = Modifier.defaultMinSize(24.dp),
                        color = BaseColors.LightGray,
                    )
                }
            } else {
                item(selectedUser.value.size) {
                    selectedUser.value.forEach {
                        Card(modifier = Modifier.defaultMinSize(minHeight = 24.dp)) {
                            Row(
                                modifier = Modifier
                                    .background(BaseColors.LightGray)
                                    .clickable {
                                        if (canChange) {
                                            val list = selectedUser.value.toMutableList()
                                            list.remove(it)
                                            selectedUser.value = list
                                            onSelect(list)
                                        }
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    it.fullName,
                                    modifier = Modifier.padding(4.dp),
                                    textAlign = TextAlign.Center
                                )
                                if (canChange) {
                                    Icon(
                                        modifier = Modifier.size(24.dp).padding(2.dp),
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = "Cancel button"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
