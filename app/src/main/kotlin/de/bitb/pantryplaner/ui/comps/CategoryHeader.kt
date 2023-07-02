package de.bitb.pantryplaner.ui.comps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.dialogs.EditCategoryDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryHeader(
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