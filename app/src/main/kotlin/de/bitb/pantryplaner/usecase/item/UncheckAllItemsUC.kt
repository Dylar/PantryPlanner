package de.bitb.pantryplaner.usecase.item

import androidx.compose.ui.graphics.Color
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.ui.base.styles.BaseColors.FilterColors
import kotlinx.coroutines.flow.first

class UncheckAllItemsUC(
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(color: Color): Resource<Unit> {
        return tryIt {
            val getItemsResp = itemRepo.getItems().first()
            if (getItemsResp is Resource.Error) {
                return@tryIt getItemsResp.castTo()
            }

            val items = getItemsResp.data ?: listOf()
            if (items.isEmpty()) {
                return@tryIt Resource.Success()
            }

            val resp =
                itemRepo.saveItems(items
                    .filter { color == FilterColors.first() || color == it.color }
                    .map { it.copy(checked = false) }
                )
            if (resp is Resource.Error) {
                return@tryIt resp.castTo()
            }
            Resource.Success()
        }
    }
}