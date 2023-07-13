package de.bitb.pantryplaner.data.model

import androidx.compose.ui.graphics.Color
import de.bitb.pantryplaner.ui.base.styles.BaseColors

data class Filter(
    val searchTerm: String = "",
    val color: Color = BaseColors.UnselectedColor
) {
    val filterByColor
        get() = color != BaseColors.UnselectedColor
    val filterByTerm
        get() = searchTerm.isNotBlank()
}
