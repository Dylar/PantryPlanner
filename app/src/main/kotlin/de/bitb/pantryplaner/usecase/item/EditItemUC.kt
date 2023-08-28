package de.bitb.pantryplaner.usecase.item

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.capitalizeFirstCharacter
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.StockItem
import kotlinx.coroutines.flow.first

class EditItemUC(
    private val itemRepo: ItemRepository,
    private val stockRepo: StockRepository,
) {
    suspend operator fun invoke(
        itemId: String,
        amount: String,
    ): Resource<Unit> {
        return tryIt {
            val itemResp = itemRepo.getItem(itemId).first()
            if (itemResp is Resource.Error) return@tryIt itemResp.castTo()

            val stockItemResp = stockRepo.getStockItems().first()
            if (stockItemResp is Resource.Error) return@tryIt stockItemResp.castTo()

            this(
                stockItemResp.data!![itemId]!!,
                itemResp.data!!,
                amount = amount,
            )
        }
    }

    suspend operator fun invoke(
        stockItem: StockItem,
        item: Item,
        name: String = item.name,
        category: String = item.category,
        color: Color = stockItem.color,
        amount: String = stockItem.amount.toString(),
        freshUntil: Long = stockItem.freshUntil,
        remindAfter: Long = stockItem.remindAfter,
    ): Resource<Unit> {
        return tryIt(
            onError = {
                when (it) {
                    is NumberFormatException -> {
                        if (amount.isEmpty()) {
                            Resource.Success()
                        } else "Not a number error".asResourceError()
                    }
                    else -> Resource.Error(it)
                }
            },
            onTry = {
                val amountDouble = amount.replace(",", ".").toDouble()
                val resp = itemRepo.saveItems(listOf(
                    item.copy(
                        name = name.capitalizeFirstCharacter(),
                        category = category,
                    )
                ))
                if (resp is Resource.Error) return@tryIt resp

                stockRepo.saveItems(listOf(
                    stockItem.copy(
                        colorHex = color.toArgb(),
                        amount = amountDouble,
                        freshUntil = freshUntil,
                        remindAfter = remindAfter,
                    )
                ))
            },
        )
    }
}