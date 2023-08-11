package de.bitb.pantryplaner.data.model

import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.Exclude
import de.bitb.pantryplaner.core.misc.parseDateTimeString
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import java.time.LocalDateTime
import java.util.*

data class Checklist(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val items: MutableList<CheckItem> = mutableListOf(),
    val finishedAt: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val creator: String = "",
    val sharedWith: List<String> = listOf(),
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

//data class ItemList(
//    val uuid: String = UUID.randomUUID().toString(),
//    val name: String = "",
//    val items: MutableList<Item> = mutableListOf(),
//)
//
//data class CheckList(
//    val uuid: String = UUID.randomUUID().toString(),
//    val name: String = "",
//    val items: MutableList<CheckItem> = mutableListOf(),
//    val finished: Boolean = false,
//)
//
//data class TemplateList(
//    val uuid: String,
//    val name: String,
//    val items: List<String>,
//)
//
//data class RecipeList(
//    val uuid: String,
//    val name: String,
//    val items: List<CheckItem>,
//)

