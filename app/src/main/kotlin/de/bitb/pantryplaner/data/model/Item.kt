package de.bitb.pantryplaner.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.firebase.firestore.Exclude
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import java.util.*

data class Item(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val category: String = "",
    val checked: Boolean = false,
    val colorHex: Int = BaseColors.LightGray.toArgb(),
    //TODO timestamp
) {
    @get:Exclude
    val color: Color
        get() = Color(colorHex)
}