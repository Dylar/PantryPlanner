package de.bitb.pantryplaner.data.model

import java.util.UUID

data class Location(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val creator: String = "",
    val createdAt: String = "",
    val sharedWith: List<String> = listOf(),
)

