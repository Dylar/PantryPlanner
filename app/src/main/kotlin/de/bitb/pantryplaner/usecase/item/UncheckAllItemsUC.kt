package de.bitb.pantryplaner.usecase.item

import androidx.compose.ui.graphics.Color
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import kotlinx.coroutines.flow.first

class UncheckAllItemsUC(
    // TODO do we need this?
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(color: Color): Result<Unit> {
        return tryIt {
            val getItemsResp = itemRepo.getUserItems().first()
            if (getItemsResp is Result.Error) return@tryIt getItemsResp.castTo()

            val itemsMap = getItemsResp.data?.groupBy { it.category } ?: mapOf()
            if (itemsMap.isEmpty()) {
                return@tryIt Result.Success()
            }

            // TODO do we need this?
//            val items = itemsMap.values
//                .fold(mutableListOf<Item>()) { previous, next -> previous.apply { addAll(next) } }
//            val resp =
//                itemRepo.saveItems(items
//                    .filter { color == FilterColors.first() || color == it.color }
//                    .map { it.apply { checked = false } }
//                )
//            if (resp is Resource.Error) {
//                return@tryIt resp.castTo()
//            }
            Result.Success()
        }
    }
}