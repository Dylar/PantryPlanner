package de.bitb.pantryplaner.ui.comps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun CircleRow(
    selectedCircleIndex: MutableStateFlow<Filter>,
    selectableColors: List<Color>
) {
    val state = selectedCircleIndex.collectAsState()
    Row(
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        for (color in selectableColors) {
            Circle(
                color = color,
                isSelected = color == state.value.color,
                onSelect = { selectedCircleIndex.update { selectedCircleIndex.value.copy(color = color) } },
            )
        }
    }
}

@Composable
fun Circle(
    color: Color,
    isSelected: Boolean,
    onSelect: (Color) -> Unit,
) {
    Icon(
        imageVector = Icons.Default.Circle,
        contentDescription = "color cycle",
        modifier = Modifier
            .background(
                if (isSelected) BaseColors.Black.copy(alpha = .4f) else Color.Transparent,
                shape = CircleShape,
            )
            .padding(4.dp)
            .clickable { onSelect(color) },
        tint = color,
    )
}
