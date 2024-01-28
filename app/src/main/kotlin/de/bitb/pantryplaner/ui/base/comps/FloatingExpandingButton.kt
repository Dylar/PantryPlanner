package de.bitb.pantryplaner.ui.base.comps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.ui.base.testTags.FloatingExpandingButtonTag
import de.bitb.pantryplaner.ui.base.testTags.testTag

@Composable
fun FloatingExpandingButton(
    buttons: @Composable ColumnScope.() -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center,
    ) {
        if (expanded) buttons()
        Spacer(modifier = Modifier.height(8.dp))
        FloatingActionButton(
            modifier = Modifier
                .testTag(FloatingExpandingButtonTag)
                .size(if (expanded) 36.dp else 64.dp),
            onClick = { expanded = !expanded }
        ) {
            Icon(
                imageVector = if (expanded) Icons.Filled.Close else Icons.Filled.Add,
                contentDescription = if (expanded) "Schließen" else "Öffnen"
            )
        }
    }
}
