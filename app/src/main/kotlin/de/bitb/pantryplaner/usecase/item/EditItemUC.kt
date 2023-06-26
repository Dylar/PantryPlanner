package de.bitb.pantryplaner.usecase.item

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.castOnError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.model.Item
import kotlinx.coroutines.flow.first

class EditItemUC(
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(itemId: String, amount: String): Resource<Unit> {
        return tryIt {
            val item = itemRepo.getItem(itemId).first()
            castOnError(item) {
                return@castOnError this(item.data!!, amount = amount)
            }
        }
    }

    suspend operator fun invoke(
        item: Item,
        name: String = item.name,
        category: String = item.category,
        color: Color = item.color,
        amount: String = item.amount.toString(),
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
                itemRepo.saveItems(
                    listOf(
                        item.copy(
                            name = name,
                            category = category,
                            colorHex = color.toArgb(),
                            amount = amountDouble,
                        )
                    )
                )
            },
        )
    }
}