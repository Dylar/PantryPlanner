package de.bitb.pantryplaner.data.model

import com.google.firebase.firestore.Exclude
import de.bitb.pantryplaner.core.misc.parseDateTimeString
import java.time.LocalDateTime
import java.util.UUID

data class Item(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val category: String = "",
    val creator: String = "",
    val createdAt: String = "",
    val sharedWith: List<String> = listOf(),
//    val tags:List<String> = listOf() // TODO tags for search etc
    // TODO price (history)
    // TODO pic for this (at first URL from Inet)
    // TODO pic to stock place (where can i find it at this stock)
    // TODO pic to market place (where can i find it at which market)
) {
    @get:Exclude
    val createDate: LocalDateTime
        get() = parseDateTimeString(createdAt)

    fun toStockItem(): StockItem = StockItem(uuid)

    fun sharedWith(userId: String): Boolean = creator == userId || sharedWith.contains(userId)
}

val List<Item>.groupByCategory: Map<String, List<Item>>
    get() = groupBy { it.category }.toSortedMap(compareBy<String> { it != "" }.thenBy { it })