package de.bitb.pantryplaner.data.model

import com.google.firebase.firestore.Exclude
import de.bitb.pantryplaner.core.misc.parseDateTimeString
import java.time.LocalDateTime
import java.util.UUID

data class Item(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val category: String = "", // TODO category as obj
    val creator: String = "",
    val createdAt: String = "",
    val sharedWith: List<String> = listOf(),
) {
    @get:Exclude
    val createDate: LocalDateTime
        get() = parseDateTimeString(createdAt)

    fun toStockItem(): StockItem = StockItem(uuid)
}
