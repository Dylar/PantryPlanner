package de.bitb.pantryplaner.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.ui.ReleaseNotesFragment
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
                LazyColumn(
                    modifier = Modifier.padding(4.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start,
                    contentPadding = PaddingValues(4.dp),
                ) {
                    items(checklists.size) { i ->
                        val checklist = checklists[i]
                        Spacer(modifier = Modifier.height(4.dp))
                        Card(
                            modifier = Modifier
                                .testTag(ChecklistTag(checklist.name))
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .padding(4.dp)
                                .clickable { onSelect(checklist) }
                        ) {
                            Text(
                                checklist.name,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 16.sp,
                                textDecoration = if (checklist.finished) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }
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
