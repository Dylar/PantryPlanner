package de.bitb.pantryplaner.usecase.item

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.model.Item

class CheckItemUC(
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(item: Item): Resource<Unit> {
        return tryIt {
            val checkItem = item.copy(checked = !item.checked)
            val resp = itemRepo.saveItems(listOf(checkItem))
            if (resp is Resource.Error) {
                resp.castTo()
            } else Resource.Success()
        }
    }
}