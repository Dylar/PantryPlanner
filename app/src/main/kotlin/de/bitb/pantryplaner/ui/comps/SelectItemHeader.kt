package de.bitb.pantryplaner.ui.comps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import de.bitb.pantryplaner.data.model.StockItem
import de.bitb.pantryplaner.ui.base.TestTags
import de.bitb.pantryplaner.ui.base.styles.BaseColors

@Composable
fun SelectItemHeader(
    stockItem: StockItem,
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
            modifier = Modifier
                .height(20.dp)
                .width(20.dp)
                .weight(.2f)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            onCheckedChange = { checkItem(item.uuid) },
            colors = CheckboxDefaults.colors(
                checkedColor = stockItem.color,
                uncheckedColor = stockItem.color
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
