package de.bitb.pantryplaner.data.model

import androidx.compose.ui.graphics.Color
import de.bitb.pantryplaner.ui.base.styles.BaseColors

data class Filter(val color: Color) {
    val colorSelected
        get() = color != BaseColors.UnselectedColor
}
