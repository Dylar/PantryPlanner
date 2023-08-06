package de.bitb.pantryplaner.usecase.item

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.capitalizeFirstCharacter
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.model.Item

class AddItemUC(
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(name: String, category: String): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                if (name.isBlank()) {
                    return@tryIt "Name darf nicht leer sein".asResourceError()
                }
                val item = Item(name = name.capitalizeFirstCharacter(), category = category)
                itemRepo.addItem(item)
            },
        )
    }
}