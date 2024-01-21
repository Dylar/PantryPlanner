package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.ui.base.comps.CircleRow
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.base.testTags.EditCategoryDialogTag
import de.bitb.pantryplaner.ui.base.testTags.testTag

@Composable
fun EditCategoryDialog(
    color: Color,
    category: String,
    onConfirm: (String, Color) -> Unit,
    onDismiss: () -> Unit,
) {
    var categoryState by remember {
        mutableStateOf(
            TextFieldValue(
                text = category,
                selection = TextRange(category.length)
            )
        )
    }
    val colorState = remember { mutableStateOf(color) }
    AlertDialog(
        modifier = Modifier.testTag(EditCategoryDialogTag.DialogTag),
        onDismissRequest = onDismiss,
        text = {
            Column {
                Text("Edit category")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .testTag(EditCategoryDialogTag.CategoryLabel)
                        .padding(4.dp),
                    singleLine = true,
                    label = { Text(stringResource(R.string.item_category)) },
                    value = categoryState,
                    onValueChange = { categoryState = it },
                    keyboardActions = KeyboardActions(
                        onDone = { onConfirm(categoryState.text, colorState.value) }
                    ),
                )
                CircleRow(
                    selectedCircle = colorState,
                    selectableColors = BaseColors.SelectableColors
                )
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.testTag(EditCategoryDialogTag.SaveButton),
                onClick = { onConfirm(categoryState.text, colorState.value) },
                content = { Text("BEARBEITEN") }
            )
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                content = { Text("ABBRECHEN") }
            )
        }
    )
}
