package de.bitb.pantryplaner.data.model

import androidx.compose.ui.graphics.Color
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.base.styles.asColor
import java.util.UUID

data class Settings(
    val uuid: String = UUID.randomUUID().toString(),
    val darkMode: Boolean? = null,
    val refreshAlert: Boolean = false,
    val categoryColors: MutableMap<String, Int> = mutableMapOf(),
    val freshUntil: Map<String, Long> = emptyMap(),
    val remindAfter: Map<String, Long> = emptyMap(),
    // TODO setting for showing small info (like User without Name etc)
) {
    fun categoryColor(item: Item): Color {
        return categoryColors[item.category]?.asColor() ?: BaseColors.LightGray
    }
//    fun freshUntilDate(itemId: String): LocalDate {
//       return LocalDate.now().minusDays(freshUntil[itemId] ?: 0L)
//    } //TODO fix this page
//
//    fun isFresh(finishDay: LocalDate): Boolean = freshUntil[uuid] == 0L ||
//            (amount != 0.0 && finishDay.isAfter(freshUntilDate))
//
//    @get:Exclude
//    val remindAfterDate: LocalDate
//        get() = LocalDate.now().minusDays(remindAfter)
//
//    fun remindIt(finishDay: LocalDate): Boolean = remindAfter[uuid] != 0L &&
//            amount == 0.0 && finishDay.isBefore(remindAfterDate)
//
//    fun isAlertable(finishDay: LocalDate): Boolean = (!isFresh(finishDay) || remindIt(finishDay))
}