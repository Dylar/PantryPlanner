package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.ui.base.testTags.ChecklistTag
import de.bitb.pantryplaner.ui.base.testTags.SelectChecklistDialogTag
import de.bitb.pantryplaner.ui.base.testTags.testTag

@Composable
fun useSelectChecklistDialog(
    showDialog: MutableState<Boolean>,
    checklists: List<Checklist>,
    onSelect: (Checklist) -> Unit,
) {
    if (showDialog.value) {
        SelectChecklistDialog(
            checklists = checklists,
            onSelect = { check ->
                onSelect(check)
                showDialog.value = false
            },
            onDismiss = { showDialog.value = false },
        )
    }
}

@Composable
private fun SelectChecklistDialog(
    checklists: List<Checklist>,
    onSelect: (Checklist) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.testTag(SelectChecklistDialogTag.DialogTag),
        onDismissRequest = onDismiss,
        text = {
            Column {
                Text("Checkliste auswählen")
                checklists.forEach { checklist ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        checklist.name,
                        modifier = Modifier
                            .testTag(ChecklistTag(checklist.name))
                            .defaultMinSize(minHeight = 48.dp)
                            .padding(12.dp)
                            .clickable { onSelect(checklist) },
                        fontSize = 16.sp,
                        textDecoration = if (checklist.finished) TextDecoration.LineThrough else TextDecoration.None
                    )
                }
            }
        },
        confirmButton = { // TODO new checklist button
//            Button(
//                modifier = Modifier.testTag(AddEditChecklistDialogTag.ConfirmButton),
//                onClick = { onConfirm(copyChecklist(), true) },
//                content = { Text(confirmButton) }
//            )
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                content = { Text("Abbrechen") }
            )
        }
    )
}
