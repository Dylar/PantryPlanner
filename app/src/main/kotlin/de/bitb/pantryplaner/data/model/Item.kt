package de.bitb.pantryplaner.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.firebase.firestore.Exclude
import de.bitb.pantryplaner.core.misc.parseDateTimeString
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class Item(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val category: String = "",
    var amount: Double = 0.0,
    val colorHex: Int = BaseColors.LightGray.toArgb(),
    val createdAt: String = "",
    var updatedAt: String = "",
    var freshUntil: Long = 0,
    var remindAfter: Long = 0,
) {
    @get:Exclude
    val color: Color
        get() = Color(colorHex)

    @get:Exclude
    val createDate: LocalDateTime
        get() = parseDateTimeString(createdAt)

    @get:Exclude
    val updateDate: LocalDateTime
        get() = parseDateTimeString(updatedAt)

    @get:Exclude
    val freshUntilDate: LocalDate
        get() = LocalDate.now().minusDays(freshUntil)

    fun isFresh(finishDay: LocalDate): Boolean = freshUntil == 0L ||
            (amount != 0.0 && finishDay.isAfter(freshUntilDate))

    @get:Exclude
    val remindAfterDate: LocalDate
        get() = LocalDate.now().minusDays(remindAfter)

    fun remindIt(finishDay: LocalDate): Boolean = remindAfter != 0L &&
            amount == 0.0 && finishDay.isBefore(remindAfterDate)

}

data class CheckItem(
    val uuid: String = "",
    var checked: Boolean = false,
    var amount: Double = 1.0,
    //TODO timestamp?
)