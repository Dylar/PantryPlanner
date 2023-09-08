package de.bitb.pantryplaner.data.model

import java.util.*

data class Stock(
    val uuid: String = UUID.randomUUID().toString(),
    val items: MutableList<StockItem> = mutableListOf(),
    val sharedWith: List<String> = emptyList(),
    // TODO add location to get multi stocks
)