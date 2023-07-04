package de.bitb.pantryplaner.ui.base.comps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun DissmissItem(
    name: String,
    color: Color,
    showRemoveDialog: MutableState<Boolean>,
    onRemove: () -> Unit,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToEnd) {
                showRemoveDialog.value = true
                true
            } else false
        }
    )

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != DismissValue.Default) {
            dismissState.reset()
        }
    }

    if (showRemoveDialog.value) {
        ConfirmDialog(
            "Entfernen?",
            "Möchten Sie \"$name\" entfernen?",
            onConfirm = {
                onRemove()
                showRemoveDialog.value = false
            },
            onDismiss = { showRemoveDialog.value = false },
        )
    }

    SwipeToDismiss(
        modifier = Modifier.padding(2.dp),
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd),
        background = { DeleteItemBackground() },
        dismissContent = {
            Card(
                elevation = 4.dp,
                border = BorderStroke(2.dp, color),
                modifier = Modifier
//                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { onClick() },
                        onLongClick = { onLongClick() },
                    ),
            ) { content() }
        },
    )
}

@Composable
fun DeleteItemBackground() {
    Card(
        elevation = 4.dp,
        modifier = Modifier
//            .fillMaxSize()
            .padding(vertical = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxSize()
                .background(BaseColors.FireRed)
                .padding(4.dp)
        ) {
            Text(
                text = "Delete",
                fontSize = 20.sp,
                color = BaseColors.White
            )
        }
    }
}