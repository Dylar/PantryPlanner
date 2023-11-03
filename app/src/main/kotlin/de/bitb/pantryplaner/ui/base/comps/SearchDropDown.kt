package de.bitb.pantryplaner.ui.base.comps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.testTags.DropDownItemTag
import de.bitb.pantryplaner.ui.base.testTags.SearchDropDownTag
import de.bitb.pantryplaner.ui.base.testTags.SharedWithTag
import de.bitb.pantryplaner.ui.base.testTags.testTag

@Composable
fun buildCategoryDropDown(
    category: MutableState<TextFieldValue>,
    categorys: List<String>,
    canChange: Boolean = true,
) {
    SearchDropDown(
        stringResource(R.string.item_category),
        category,
        canChange = canChange,
        addUnknownOption = true,
        options = categorys,
        optionMapper = { it },
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
        val selectedState = remember { mutableStateOf(TextFieldValue("")) }
        SearchDropDown(
            "Mit Benutzer teilen",
            selectedState,
            canChange = canChange,
            clearOnSelection = true,
            options = users.filter { !selectedUser.value.contains(it) },
            optionMapper = { it.fullName },
        ) { selection ->
            val user = users.first { it.fullName == selection }
            //TODO oh oh was passiert wenn wir uns ne liste teilen und ich hab den user nicht den du teilst ... aaahhh xD
            val list = selectedUser.value.toMutableList()
            if (!list.remove(user)) {
                list.add(user)
            }
            selectedUser.value = list
            onSelect(list)
        }
    }
    ConnectedUser(emptyText, selectedUser, canChange, onSelect)
}

@Composable
fun buildStockDropDown(
    selectedStock: MutableState<Stock>,
    stocks: List<Stock>,
    canChange: Boolean = true,
    onSelect: (Stock) -> Unit = {},
) {
    fun optionMapper(stock: Stock): String = stock.name
    val selectedState = remember {
        val option = optionMapper(selectedStock.value)
        mutableStateOf(TextFieldValue(option))
    }
    SearchDropDown(
        stringResource(R.string.choose_stock),
        selectedState,
        canChange = canChange,
        options = stocks,
        optionMapper = ::optionMapper
    ) { selection ->
        val stock = stocks.first { it.name == selection }
        selectedStock.value = stock
        onSelect(stock)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun <T> SearchDropDown(
    hint: String,
    selectedState: MutableState<TextFieldValue>,
    options: List<T>,
    optionMapper: (T) -> String,
    canChange: Boolean = true,
    addUnknownOption: Boolean = false,
    clearOnSelection: Boolean = false,
    onConfirm: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val addText = "\"${selectedState.value.text}\" wird neu angelegt"
    val input = selectedState.value.text.lowercase()
    val opts = options
        .associateWith { optionMapper(it) }
        .asSequence()
        .filter {
            val text = it.value.lowercase()
            if (text.isBlank()) false
            else input.isBlank() || text.contains(input)
        }
        .sortedBy {
            val text = it.value
            !text.lowercase().startsWith(input)
        }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = { if (canChange) expanded = !expanded }
    ) {
        TextField(
            readOnly = !canChange,
            enabled = canChange,
            value = selectedState.value,
            onValueChange = { selectedState.value = it },
            modifier = Modifier
                .fillMaxWidth()
//                .menuAnchor()
                .testTag(SearchDropDownTag(hint)),
            label = { Text(hint) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { onConfirm(selectedState.value.text) }),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            if (addUnknownOption &&
                input.isNotBlank() &&
                opts.none { it.value.lowercase() == input }
            ) {
                DropdownMenuItem(
                    modifier = Modifier
                        .padding(2.dp)
//                        .background(BaseColors.FireRed.copy(alpha = .5f))
                        .testTag(DropDownItemTag(addText)),
                    onClick = { expanded = false },
                    content = { Text(text = addText) },
                )
            }

            opts.forEach { option ->
                val mappedOption = option.value
                DropdownMenuItem(
                    modifier = Modifier
                        .padding(2.dp)
//                        .background(BaseColors.FireRed.copy(alpha = .5f))
                        .testTag(DropDownItemTag(mappedOption)),
                    onClick = {
                        selectedState.value =
                            if (clearOnSelection) {
                                TextFieldValue("")
                            } else {
                                TextFieldValue(
                                    mappedOption,
                                    TextRange(mappedOption.length)
                                )
                            }
                        onConfirm(mappedOption)
                        expanded = false
                    },
                    content = { Text(text = mappedOption) },
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
                        modifier = Modifier
                            .testTag(SharedWithTag.NothingShared)
                            .defaultMinSize(24.dp),
                    )
                }
            } else {
                val userList = selectedUser.value
                items(userList.size) {
                    val user = userList[it]
                    Card(
                        modifier = Modifier
                            .testTag(SharedWithTag.SharedChip(user.fullName))
                            .defaultMinSize(minHeight = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier
//                                .background(BaseColors.LightGray)
                                .clickable {
                                    if (canChange) {
                                        val list = selectedUser.value.toMutableList()
                                        list.remove(user)
                                        selectedUser.value = list
                                        onSelect(list)
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                user.fullName,
                                modifier = Modifier.padding(4.dp),
                                textAlign = TextAlign.Center
                            )
                            if (canChange) {
                                Icon(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(2.dp),
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
