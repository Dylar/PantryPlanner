package de.bitb.pantryplaner.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import de.bitb.pantryplaner.ui.base.styles.BaseColors

data class Item(
    val name: String = "",
    val checked: Boolean = false,
    val colorHex: Int = BaseColors.LightGray.toArgb(),
    //TODO timestamp
) {
    val color: Color
        get() = Color(colorHex)
}