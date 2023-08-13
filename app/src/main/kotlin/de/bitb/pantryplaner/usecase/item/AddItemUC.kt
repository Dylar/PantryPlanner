package de.bitb.pantryplaner.usecase.item

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.capitalizeFirstCharacter
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Item
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class AddItemUC(
    private val itemRepo: ItemRepository,
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(name: String, category: String): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                if (name.isBlank()) {
                    return@tryIt "Name darf nicht leer sein".asResourceError()
                }

                val user = userRepo.getUser().first()
                if (user is Resource.Error) return@tryIt user.castTo(false)

                val item = Item(
                    name = name.capitalizeFirstCharacter(),
                    category = category,
                    creator = user.data!!.uuid,
                )
                itemRepo.addItem(item)
            },
        )
    }
}