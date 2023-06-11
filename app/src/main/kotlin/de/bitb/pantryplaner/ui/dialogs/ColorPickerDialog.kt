package de.bitb.pantryplaner.ui.dialogs
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.KeyboardActions
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.focus.FocusRequester
//import androidx.compose.ui.focus.focusRequester
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.dp
//import de.bitb.pantryplaner.R
//import de.bitb.pantryplaner.ui.base.composable.CircleRow
//import de.bitb.pantryplaner.ui.base.styles.BaseColors
//
//@Composable
//fun AddDialog(onConfirm: (String, Boolean) -> Unit, onDismiss: () -> Unit) {
//    var name by remember { mutableStateOf("") }
//    val selectedCircleIndex = remember { mutableStateOf(0) }
//    val focusRequester = remember { FocusRequester() }
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Add Item") },
//        text = {
//            Column {
//                OutlinedTextField(
//                    modifier = Modifier
//                        .padding(top = 32.dp, start = 16.dp, end = 16.dp)
//                        .focusRequester(focusRequester),
//                    singleLine = true,
//                    label = { Text(stringResource(R.string.item_name)) },
//                    value = name,
//                    onValueChange = { name = it },
//                    keyboardActions = KeyboardActions(onDone = {
//                        onConfirm(name, false)
//                        name = ""
//                    }),
//                )
//                CircleRow(
//                    circleColors = listOf(
//                        BaseColors.FireRed,
//                        BaseColors.ZergPurple,
//                        BaseColors.BabyBlue,
//                        BaseColors.AdultBlue,
//                        BaseColors.SunYellow,
//                        BaseColors.LightGreen,
//                    ),
//                    selectedCircleIndex = selectedCircleIndex,
//                )
//            }
//        },
//        confirmButton = {
//            Button(
//                onClick = { onConfirm(name, true) },
//                content = { Text("ADD") }
//            )
//        },
//        dismissButton = {
//            Button(
//                onClick = onDismiss,
//                content = { Text("CANCEL") }
//            )
//        }
//    )
//    LaunchedEffect(Unit) {
//        focusRequester.requestFocus()
//    }
//}
