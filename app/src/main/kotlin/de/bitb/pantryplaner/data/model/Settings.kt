package de.bitb.pantryplaner.data.model

import java.util.UUID

data class Settings(
    val uuid: String = UUID.randomUUID().toString(),
    val darkMode: Boolean? = null,
    val refreshAlert: Boolean = true,
)