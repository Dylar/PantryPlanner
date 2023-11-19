package de.bitb.pantryplaner.data.model

import java.util.UUID

// TODO Receipe page show items you need (red) and which you have (green)
data class Recipe(
    val uuid: String = UUID.randomUUID().toString(),
    var name: String = "",
    var category: String = "",
    val items: MutableList<RecipeItem> = mutableListOf(),
    val creator: String = "",
    val sharedWith: MutableList<String> = mutableListOf(),
    val createdAt: String = "",
    val updatedAt: String = "",
) {
    fun isNew() = createdAt == ""
    fun sharedWith(userId: String): Boolean = creator == userId || sharedWith.contains(userId)
}

data class RecipeItem(
    val uuid: String = "",
    var amount: Double = 1.0,
)

val List<Recipe>.groupByCategory: Map<String, List<Recipe>>
    get() = groupBy { it.category }.toSortedMap(compareBy<String> { it != "" }.thenBy { it })