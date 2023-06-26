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
    var amount: Double = 0.0,
    val colorHex: Int = BaseColors.LightGray.toArgb(),
    //TODO timestamp
) {
    @get:Exclude
    val color: Color
        get() = Color(colorHex)
}

data class CheckItem(
    val uuid: String = "",
    var checked: Boolean = false,
    var amount: Double = 1.0,
    //TODO timestamp
)