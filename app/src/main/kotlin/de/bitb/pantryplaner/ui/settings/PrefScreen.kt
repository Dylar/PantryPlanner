package de.bitb.pantryplaner.ui.settings

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.ui.base.styles.PantryAppTheme

@Composable
fun FullscreenPreference(
    title: String,
    items: List<PreferenceItem>,
    selectedItem: PreferenceItem,
    onItemSelected: (PreferenceItem) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            Surface(elevation = 4.dp) {
                TopAppBar(
                    title = { Text(text = title) },
                    contentColor = MaterialTheme.colors.onSurface,
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp,
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            content = {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = null,
                                )
                            },
                        )
                    },
                    modifier = Modifier.statusBarsPadding(),
                )
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize(),
        ) {
            items(items) { item ->
                RadioPreferenceItem(
                    title = item.title,
                    subtitle = item.subtitle,
                    selected = selectedItem == item,
                    onClick = { onItemSelected(item) },
                )
            }
        }
    }
}

@Composable
fun RadioPreferenceItem(
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
            )
            .padding(
                vertical = 8.dp,
                horizontal = 16.dp,
            ),
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            interactionSource = interactionSource,
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(weight = 1f, fill = true),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
            )

            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                )
            }
        }
    }
}

@Preview
@Composable
private fun RadioPreferenceItemPreview() {
    var selectedItem by remember { mutableStateOf(0) }

    PantryAppTheme(useDarkTheme = true) {
        Surface(color = MaterialTheme.colors.background) {
            Column {
                RadioPreferenceItem(
                    title = "Lorem ipsum",
                    subtitle = "lorem",
                    selected = selectedItem == 0,
                    onClick = {
                        selectedItem = 0
                    },
                )

                RadioPreferenceItem(
                    title = "Dolor sit amet",
                    subtitle = "lorem",
                    selected = selectedItem == 1,
                    onClick = {
                        selectedItem = 1
                    },
                )

                RadioPreferenceItem(
                    title = "Hello",
                    subtitle = "lorem",
                    selected = selectedItem == 2,
                    onClick = {
                        selectedItem = 2
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun FullScreenPreferencePreview() {
    val items = listOf(
        PreferenceItem("Lorem ipsum", "lorem"),
        PreferenceItem("Dolor sit amet", "dolor"),
        PreferenceItem("Foo bar", "foobar"),
    )
    var selectedItem by remember { mutableStateOf(items.first()) }

    PantryAppTheme(useDarkTheme = true) {
        Surface(color = MaterialTheme.colors.background) {
            FullscreenPreference(
                title = "Lorem ipsum setting",
                items = items,
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it },
                onNavigateBack = { },
            )
        }
    }
}