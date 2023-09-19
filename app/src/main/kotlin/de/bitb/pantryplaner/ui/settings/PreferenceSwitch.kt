package de.bitb.pantryplaner.ui.settings

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceSwitch(
    item: PreferenceItem,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled,
                onClick = { onChange(!checked) },
            )
            .padding(all = 16.dp),
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true),
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.body1,
                color = if (!enabled) MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled) else Color.Unspecified,
            )

            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.body2,
                color = if (!enabled) MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled) else MaterialTheme.colors.onSurface.copy(
                    alpha = ContentAlpha.medium
                ),
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onChange,
            interactionSource = interactionSource,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SwitchPreferencePreview() {
    var checked by remember { mutableStateOf(value = true) }

    PreferenceSwitch(
        PreferenceItem("Dark theme", "Lorem ipsum dolor sit amet"),
        checked = checked,
        onChange = { checked = it },
    )
}