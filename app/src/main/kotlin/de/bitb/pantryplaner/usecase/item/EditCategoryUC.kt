package de.bitb.pantryplaner.usecase.item

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.capitalizeFirstCharacter
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import kotlinx.coroutines.flow.first

class EditCategoryUC(
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(
        previousCategory: String,
        newCategory: String,
        color: Color,
    ): Resource<Unit> {
        return tryIt {
            val itemsResp = itemRepo.getItems().first()
            if (itemsResp is Resource.Error) return@tryIt itemsResp.castTo()

            val itemsMap = (itemsResp.data ?: mapOf())
            val itemsToEdit = itemsMap[previousCategory]?.map {
                it.copy(
                    category = newCategory.capitalizeFirstCharacter(),
                    colorHex = color.toArgb()
                )
            }
            if (itemsToEdit?.isEmpty() != false) Resource.Success()
            else itemRepo.saveItems(itemsToEdit)
        }
    }
}