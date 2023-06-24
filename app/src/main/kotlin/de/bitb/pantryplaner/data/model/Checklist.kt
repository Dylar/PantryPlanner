package de.bitb.pantryplaner.data.model

import java.util.*

data class Checklist(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val items: MutableList<CheckItem> = mutableListOf(),
    val finished: Boolean = false,
)

data class TemplateList(val uuid: String, val name: String, val items: List<String>)