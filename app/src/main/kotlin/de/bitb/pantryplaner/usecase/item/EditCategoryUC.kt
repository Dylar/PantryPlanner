package de.bitb.pantryplaner.usecase.item

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.capitalizeFirstCharacter
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.StockItem
import kotlinx.coroutines.flow.first

class EditCategoryUC(
    private val itemRepo: ItemRepository,
    private val stockRepo: StockRepository,
) {
    suspend operator fun invoke(
        previousCategory: String,
        newCategory: String,
        color: Color,
    ): Resource<Unit> {
        return tryIt {
            val itemsResp = itemRepo.getItems().first()
            if (itemsResp is Resource.Error) return@tryIt itemsResp.castTo()

            val stockResp = stockRepo.getStockItems().first() //TODO only because of color
            if (stockResp is Resource.Error) return@tryIt stockResp.castTo()

            val itemsMap = itemsResp.data ?: mapOf()
            val stockItems = stockResp.data ?: mapOf()

            val itemsToEdit = mutableListOf<Item>()
            val stockItemsToEdit = mutableListOf<StockItem>()

            itemsMap[previousCategory]?.forEach {
                itemsToEdit.add(it.copy(category = newCategory.capitalizeFirstCharacter()))
                stockItems[it.uuid]?.let { stockItem ->
                    stockItemsToEdit.add(stockItem.copy(colorHex = color.toArgb()))
                }
            }

            if (itemsToEdit.isNotEmpty()) {
                val saveResp = itemRepo.saveItems(itemsToEdit)
                if (saveResp is Resource.Error) return@tryIt saveResp
            }

            if (stockItemsToEdit.isNotEmpty()) {
                val saveResp = stockRepo.saveItems(stockItemsToEdit)
                if (saveResp is Resource.Error) return@tryIt saveResp
            }

            Resource.Success()
        }
    }
}