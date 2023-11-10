package de.bitb.pantryplaner.ui.base.comps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bitb.pantryplaner.ui.base.testTags.GridLayoutTag
import de.bitb.pantryplaner.ui.base.testTags.ListLayoutTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import de.bitb.pantryplaner.ui.dialogs.EditCategoryDialog

const val NO_CATEGORY = "Keine Kategorie"

fun LazyGridScope.stickyGridHeader(
    content: @Composable LazyGridItemScope.() -> Unit,
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
        contentAlignment = Alignment.Center, modifier = Modifier.padding(innerPadding)
    ) {
        val contentPadding = PaddingValues(
            top = 4.dp,
            bottom = 124.dp,
            start = 4.dp,
            end = 4.dp,
        )
        if (showGridLayout.value) {
            LazyVerticalGrid(
                GridCells.Fixed(if (items.size == 1 && items.values.first().size == 1) 1 else 2),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(GridLayoutTag),
                verticalArrangement = Arrangement.Top,
                horizontalArrangement = Arrangement.Center,
                contentPadding = contentPadding,
            ) {
                items.forEach { (header, list) ->
                    val headerText = header.ifBlank { NO_CATEGORY }
                    stickyGridHeader {
                        GridListHeader(
                            headerText,
                            headerColor(list.first()),
                            showItems,
                            if (header.isBlank()) null else editHeader,
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
                    .testTag(ListLayoutTag),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = contentPadding,
            ) {
                items.forEach { (header, list) ->
                    val headerText = header.ifBlank { NO_CATEGORY }
                    stickyHeader {
                        GridListHeader(
                            headerText,
                            headerColor(list.first()),
                            showItems,
                            if (header.isBlank()) null else editHeader,
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
fun GridListHeader(
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
            modifier = Modifier.combinedClickable(
                onClick = {
                    val value = showItems[category]
                    showItems[category] = !(value ?: true)
                },
                onLongClick = { if (onEdit != null) showEditDialog = true })
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    category,
                    modifier = Modifier.drawBehind {
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
                        .padding(start = 4.dp)
                        .size(16.dp),
                    contentDescription = "",
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