package de.bitb.pantryplaner.usecase.item

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.capitalizeFirstCharacter
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Item
import kotlinx.coroutines.flow.first

class EditItemUC(
    private val userRepo: UserRepository,
    private val itemRepo: ItemRepository,
) {

    suspend operator fun invoke(
        item: Item,
        name: String = item.name,
        category: String = item.category,
    ): Result<Unit> {
        return tryIt {
            val user = userRepo.getUser().first()
            if (user is Result.Error) return@tryIt user.castTo()
            if (user.data!!.uuid != item.creator)
                return@tryIt "Nur der Ersteller kann das Item ändern".asError()
            itemRepo.saveItems(
                listOf(
                    item.copy(
                        name = name.capitalizeFirstCharacter(),
                        category = category,
                    )
                )
            )
        }
    }
}