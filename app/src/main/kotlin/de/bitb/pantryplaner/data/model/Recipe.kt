package de.bitb.pantryplaner.data.model

import java.util.UUID

// TODO Receipe page show items you need (red) and which you have (green)
data class Recipe(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val category: String = "",
    val items: List<CheckItem> = emptyList(),
    val creator: String = "",
    val sharedWith: List<String> = listOf(),
    val createdAt: String = "",
    val updatedAt: String = "",
) {
    fun sharedWith(userId: String): Boolean = creator == userId || sharedWith.contains(userId)
}

val List<Recipe>.groupByCategory: Map<String, List<Recipe>>
    get() = groupBy { it.category }.toSortedMap(compareBy<String> { it != "" }.thenBy { it })