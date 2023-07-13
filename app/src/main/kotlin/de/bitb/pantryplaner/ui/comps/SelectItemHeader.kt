package de.bitb.pantryplaner.ui.comps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.TestTags
import de.bitb.pantryplaner.ui.base.styles.BaseColors

@Composable
fun SelectItemHeader(
    item: Item,
    isChecked: Boolean,
    strikeChecked: Boolean = false,
    checkItem: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .testTag(TestTags.SelectItemHeader(item.name).name)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .background(BaseColors.LightGray.copy(alpha = .1f)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            isChecked,
            modifier = Modifier.height(20.dp).width(20.dp)
                .weight(.2f)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            onCheckedChange = { checkItem(item.uuid) },
            colors = CheckboxDefaults.colors(
                checkedColor = item.color,
                uncheckedColor = item.color
            )
        )
        Text(
            item.name,
            modifier = Modifier
                .weight(.8f)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            textDecoration = if (strikeChecked && isChecked) TextDecoration.LineThrough else TextDecoration.None
        )
    }
}
