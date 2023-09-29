package de.bitb.pantryplaner.usecase.item

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.capitalizeFirstCharacter
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.StockItem
import kotlinx.coroutines.flow.first

class EditItemUC(
    private val userRepo: UserRepository,
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

            val stockItemResp = stockRepo.getStocks().first()
            if (stockItemResp is Resource.Error) return@tryIt stockItemResp.castTo()

            this(
                stockItemResp.data!!.first().items.associateBy { it.uuid }[itemId]!!,
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
                val user = userRepo.getUser().first()
                if (user is Resource.Error) return@tryIt user.castTo()
                if (user.data!!.uuid != item.creator)
                    return@tryIt "Nur der Ersteller kann das Item ändern".asResourceError()
                //TODO prevent non-creator, but allow stock specific changes -> or we split it
                val amountDouble = amount.replace(",", ".").toDouble()
                val resp = itemRepo.saveItems(
                    listOf(
                        item.copy(
                            name = name.capitalizeFirstCharacter(),
                            category = category,
                        )
                    )
                )
                if (resp is Resource.Error) return@tryIt resp

                val stocks = stockRepo.getStocks().first()
                if (stocks is Resource.Error) return@tryIt stocks.castTo()

                //TODO this will change ;)
                val stock =
                    stocks.data?.first { stock -> stock.items.firstOrNull { it.uuid == stockItem.uuid } != null }!!
                stock.items.replaceAll {
                    if (it.uuid == stockItem.uuid)
                        stockItem.copy(
                            colorHex = color.toArgb(),
                            amount = amountDouble,
                            freshUntil = freshUntil,
                            remindAfter = remindAfter,
                        )
                    else it
                }
                stockRepo.saveStocks(listOf(stock))
            },
        )
    }
}