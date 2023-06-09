package de.bitb.pantryplaner.usecase.item

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.model.Item

class AddItemUC(
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(name: String): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                val item = Item(name)
                itemRepo.addItem(item)
            },
        )
    }
}