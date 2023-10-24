package de.bitb.pantryplaner.ui.base.comps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog

@Composable
fun dissmissItem(
    name: String,
    color: Color,
    onSwipe: () -> Unit,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    swipeItem(
        color,
        "Entfernen?",
        "Möchten Sie \"$name\" entfernen?",
        "Entfernen",
        onSwipe,
        onClick,
        onLongClick,
        content,
    )
}

@Composable
fun clearItem(
    name: String,
    color: Color,
    onSwipe: () -> Unit,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    swipeItem(
        color,
        "Zurücksetzen?",
        "Möchten Sie die Anzahl in ihrem Bestand von \"$name\" auf 0 setzen?",
        "Anzahl auf 0",
        onSwipe,
        onClick,
        onLongClick,
        content,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun swipeItem(
    color: Color,
    swipeConfirmTitle: String,
    swipeConfirmMessage: String,
    onSwipeText: String,
    onSwipe: () -> Unit,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val showRemoveDialog = remember { mutableStateOf(false) }
    val dismissState = rememberDismissState(
        confirmValueChange = {
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
            swipeConfirmTitle,
            swipeConfirmMessage,
            onConfirm = {
                onSwipe()
                showRemoveDialog.value = false
            },
            onDismiss = { showRemoveDialog.value = false },
        )
    }

    SwipeToDismiss(
        modifier = Modifier.padding(2.dp),
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd),
        background = { swipeBackground(onSwipeText) },
        dismissContent = {
            Card(
                border = BorderStroke(2.dp, color),
                modifier = Modifier
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
private fun swipeBackground(text: String) {
    Card(
        modifier = Modifier
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
                text = text,
                fontSize = 20.sp,
                color = BaseColors.White
            )
        }
    }
}