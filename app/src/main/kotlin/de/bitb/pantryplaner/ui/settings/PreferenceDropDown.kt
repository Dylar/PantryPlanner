package de.bitb.pantryplaner.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun <T> PreferenceDropDown(
    title: String,
    items: List<Pair<T, String>>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var dropDownExpanded by remember { mutableStateOf(value = false) }

    PreferenceComposable(
        PreferenceItem(
            title = title,
            subtitle = items.first { it.first == selectedItem }.second,
        ),
        onClick = {
            dropDownExpanded = true
        },
        modifier = modifier
            .background(
                color = if (dropDownExpanded)
                    MaterialTheme.colors.primary.copy(alpha = 0.2f)
                else
                    Color.Unspecified
            ),
        enabled = enabled,
    )

    Box {
        DropdownMenu(
            expanded = dropDownExpanded,
            onDismissRequest = { dropDownExpanded = !dropDownExpanded },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        dropDownExpanded = false

                        onItemSelected(item.first)
                    },
                    modifier = Modifier
                        .background(
                            color = if (selectedItem == item.first)
                                MaterialTheme.colors.primary.copy(alpha = 0.3f)
                            else
                                Color.Unspecified,
                        ),
                    content = {
                        Text(
                            text = item.second,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DropDownPreferencePreview() {
    val themes = listOf(
        "system" to "System default",
        "light" to "Light",
        "dark" to "Dark",
    )
    var selectedTheme by remember { mutableStateOf(value = themes[0]) }

    PreferenceDropDown(
        title = "Theme",
        items = themes,
        selectedItem = selectedTheme,
        onItemSelected = { selected ->
            selectedTheme = themes.first { it.first == selected }
        },
    )
}