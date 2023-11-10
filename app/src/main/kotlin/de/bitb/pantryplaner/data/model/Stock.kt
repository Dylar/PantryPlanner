package de.bitb.pantryplaner.data.model

import com.google.firebase.firestore.Exclude
import de.bitb.pantryplaner.core.misc.parseDateTimeString
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Stock(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val creator: String = "",
    val createdAt: String = "",
    val sharedWith: List<String> = listOf(),
    val items: MutableList<StockItem> = mutableListOf(),
    val isDeleted: Boolean = false,
)

data class StockItem(
    val uuid: String = "",
    var amount: Double = 0.0,
    var updatedAt: String = "",
) {

    @get:Exclude
    val updateDate: LocalDateTime
        get() = parseDateTimeString(updatedAt)

    //TODO fix this page
    @get:Exclude
    val freshUntilDate: LocalDate
        get() = LocalDate.now()//.minusDays(freshUntil)

    fun isFresh(finishDay: LocalDate): Boolean = //freshUntil == 0L ||
        (amount != 0.0 && finishDay.isAfter(freshUntilDate))

    @get:Exclude
    val remindAfterDate: LocalDate
        get() = LocalDate.now()//.minusDays(remindAfter)

    fun remindIt(finishDay: LocalDate): Boolean = //remindAfter != 0L &&
        amount == 0.0 && finishDay.isBefore(remindAfterDate)

    fun isAlertable(finishDay: LocalDate): Boolean = (!isFresh(finishDay) || remindIt(finishDay))
}