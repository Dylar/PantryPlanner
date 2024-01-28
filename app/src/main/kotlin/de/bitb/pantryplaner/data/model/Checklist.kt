package de.bitb.pantryplaner.data.model

import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.Exclude
import de.bitb.pantryplaner.core.misc.parseDateTimeString
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import java.time.LocalDateTime
import java.util.UUID

data class Checklist(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val items: List<CheckItem> = listOf(),
    val finishedAt: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val creator: String = "",
    val stock: String = "",
    val sharedWith: List<String> = listOf(),
    // TODO categorys?
) {
    @get:Exclude
    val progress: String
        get() {
            val checkedItems = items.filter { it.checked }.size
            return "$checkedItems/${items.size}"
        }

    @get:Exclude
    val color: Color
        get() = if (finished) BaseColors.AdultBlue else BaseColors.BabyBlue

    @get:Exclude
    val createDate: LocalDateTime
        get() = parseDateTimeString(createdAt)

    @get:Exclude
    val updateDate: LocalDateTime
        get() = parseDateTimeString(updatedAt)

    @get:Exclude
    val finishDate: LocalDateTime
        get() = parseDateTimeString(finishedAt)

    @get:Exclude
    val finished: Boolean
        get() = finishedAt.isNotBlank()
}

data class CheckItem(
    val uuid: String = "",
    var checked: Boolean = false,
    var amount: Double = 1.0,
    //TODO timestamp?
)
