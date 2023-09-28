package de.bitb.pantryplaner.data.model

import java.util.UUID

data class Stock(
    val uuid: String = UUID.randomUUID().toString(),
    val location: String = "",
    val items: MutableList<StockItem> = mutableListOf(),
)