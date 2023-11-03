package de.bitb.pantryplaner.usecase.item

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
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
    suspend operator fun invoke(item: Item): Result<Boolean> {
        return tryIt(
            onError = { Result.Error(it, false) },
            onTry = {
                if (item.name.isBlank()) {
                    return@tryIt "Name darf nicht leer sein".asError()
                }

                val user = userRepo.getUser().first()
                if (user is Result.Error) return@tryIt user.castTo(false)

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