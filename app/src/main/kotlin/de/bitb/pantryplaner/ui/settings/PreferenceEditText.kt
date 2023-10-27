package de.bitb.pantryplaner.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PreferenceEditText(
    item: PreferenceItem,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
    ) {
        Column(
            modifier = Modifier.weight(weight = 3f, fill = true),
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.body1,
                color = if (!enabled) MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled) else Color.Unspecified,
            )

            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.body2,
                color = if (!enabled) MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled) else MaterialTheme.colors.onSurface.copy(
                    alpha = ContentAlpha.medium
                ),
            )
        }

        val keyboardController = LocalSoftwareKeyboardController.current
        TextField(
            modifier = Modifier.weight(weight = 1f, fill = true),
            value = value,
            onValueChange = onChange,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = keyboardType,
            ),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            maxLines = 1,
            singleLine = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TextPreferencePreview() {
    var text by remember { mutableStateOf("value = true") }

    PreferenceEditText(
        PreferenceItem("Text Setting", "Lorem ipsum dolor sit amet"),
        value = text,
        onChange = { text = it },
    )
}