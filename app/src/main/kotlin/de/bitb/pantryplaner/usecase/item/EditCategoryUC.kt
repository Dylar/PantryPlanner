package de.bitb.pantryplaner.usecase.item

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import kotlinx.coroutines.flow.first

class EditCategoryUC(
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(previousCategory: String, newCategory: String): Resource<Unit> {
        return tryIt {
            val itemsResp = itemRepo.getItems().first()
            if (itemsResp is Resource.Error) {
                return@tryIt itemsResp.castTo()
            }

            val itemsMap = (itemsResp.data ?: mapOf())
            val itemsToEdit = itemsMap[previousCategory]?.map { it.copy(category = newCategory) }
            if (itemsToEdit?.isEmpty() != false) Resource.Success()
            else itemRepo.saveItems(itemsToEdit)
        }
    }
}