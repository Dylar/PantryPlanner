package de.bitb.pantryplaner.ui.base.comps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bitb.pantryplaner.ui.base.TestTags
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.dialogs.EditCategoryDialog

private fun LazyGridScope.stickyGridHeader(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> GridListLayout(
    innerPadding: PaddingValues,
    showGridLayout: MutableState<Boolean>,
    items: Map<String, List<T>>,
    headerColor: (T) -> Color,
    editHeader: ((String, String, Color) -> Unit)? = null,
    buildItem: @Composable (String, T) -> Unit,
) {
    val showItems = remember { mutableStateMapOf<String, Boolean>() }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(innerPadding)
    ) {
        val contentPadding = PaddingValues(
            top = 4.dp,
            bottom = 124.dp,
            start = 4.dp,
            end = 4.dp,
        )
        if (showGridLayout.value) {
            LazyVerticalGrid(
                GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(TestTags.GridListLayout.Grid.name),
                verticalArrangement = Arrangement.Top,
                horizontalArrangement = Arrangement.Center,
                contentPadding = contentPadding,
            ) {
                items.forEach { (header, list) ->
                    val headerText = header.ifBlank { "Keine" }
                    stickyGridHeader {
                        GridListHeader(
                            headerText,
                            headerColor(list.first()),
                            showItems,
                            editHeader,
                        )
                    }
                    if (showItems[headerText] != false) {
                        items(list.size) { buildItem(header, list[it]) }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(TestTags.GridListLayout.List.name),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = contentPadding,
            ) {
                items.forEach { (header, list) ->
                    val headerText = header.ifBlank { "Keine" }
                    stickyHeader {
                        GridListHeader(
                            headerText,
                            headerColor(list.first()),
                            showItems,
                            editHeader,
                        )
                    }
                    if (showItems[headerText] != false) {
                        items(list.size) { buildItem(header, list[it]) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GridListHeader(
    category: String,
    color: Color,
    showItems: SnapshotStateMap<String, Boolean>,
    onEdit: ((String, String, Color) -> Unit)? = null,
) {
    var showEditDialog by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            border = BorderStroke(2.dp, color),
            modifier = Modifier
                .combinedClickable(
                    onClick = {
                        val value = showItems[category]
                        showItems[category] = !(value ?: true)
                    },
                    onLongClick = { showEditDialog = true }
                )
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    category,
                    modifier = Modifier
                        .drawBehind {
                            val strokeWidthPx = 1.dp.toPx()
                            val verticalOffset = size.height - 2.sp.toPx()
                            drawLine(
                                color = color,
                                strokeWidth = strokeWidthPx,
                                start = Offset(0f, verticalOffset),
                                end = Offset(size.width, verticalOffset)
                            )
                        },
                    textAlign = TextAlign.Center,
                )
                Icon(
                    if (showItems[category] != false) Icons.Default.ArrowCircleDown else Icons.Default.ArrowCircleUp,
                    modifier = Modifier
                        .padding(start = 4.dp).size(16.dp),
                    contentDescription = "",
                    tint = BaseColors.LightGray
                )
            }
        }
    }

    if (onEdit != null && showEditDialog) {
        EditCategoryDialog(
            color,
            category,
            onConfirm = { cat, col ->
                onEdit(category, cat, col)
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false },
        )
    }
}