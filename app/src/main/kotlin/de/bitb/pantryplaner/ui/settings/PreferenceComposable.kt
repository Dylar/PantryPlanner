package de.bitb.pantryplaner.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceComposable(
    item: PreferenceItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick,
            )
            .padding(all = 16.dp),
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (!enabled) MaterialTheme.colorScheme.onSurface.copy() else Color.Unspecified,
        )

        Text(
            text = item.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = //TODO
            if (!enabled) MaterialTheme.colorScheme.onSurface.copy()
            else MaterialTheme.colorScheme.onSurface.copy(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegularPreferencePreview() {
    PreferenceComposable(
        item = PreferenceItem("Advanced settings", "Lorem ipsum dolor sit amet"),
        onClick = { },
    )
}