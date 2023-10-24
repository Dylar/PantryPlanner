package de.bitb.pantryplaner.ui.base.comps

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.ui.base.testTags.SearchBarTag
import de.bitb.pantryplaner.ui.base.testTags.testTag

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    showSearchBar: MutableState<Boolean>,
    isSearching: Boolean,
    initialValue: String,
    onSearch: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var text by remember {
        mutableStateOf(TextFieldValue(initialValue, TextRange(initialValue.length)))
    }

    TextField(
        value = text,
        onValueChange = {
            text = it
            onSearch(it.text)
        },
        label = { Text("Suche") },
        leadingIcon = {
            if (isSearching) LoadingIndicator(
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
            )
            else Icon(Icons.Filled.Search, contentDescription = null)
        },
        modifier = Modifier
            .testTag(SearchBarTag)
            .fillMaxWidth()
            .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            keyboardController?.hide()
            focusManager.clearFocus()
            showSearchBar.value = false
        })
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
