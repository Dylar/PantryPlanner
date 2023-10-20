package de.bitb.pantryplaner.usecase.item

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.capitalizeFirstCharacter
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Item
import kotlinx.coroutines.flow.first

class CreateItemUC(
    private val userRepo: UserRepository,
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(item: Item): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                if (item.name.isBlank()) {
                    return@tryIt "Name darf nicht leer sein".asResourceError()
                }

                val user = userRepo.getUser().first()
                if (user is Resource.Error) return@tryIt user.castTo(false)

                itemRepo.addItem(
                    item.copy(
                        name = item.name.capitalizeFirstCharacter(),
                        category = item.category,
                        creator = user.data!!.uuid,
                    ),
                )
            },
        )
    }
}