package de.bitb.pantryplaner.usecase.item

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Item
import kotlinx.coroutines.flow.first

class ShareItemUC(
    private val userRepo: UserRepository,
    private val itemRepo: ItemRepository,
) {

    suspend operator fun invoke(
        item: Item,
    ): Result<Unit> {
        return tryIt {
            val user = userRepo.getUser().first()
            if (user is Result.Error) return@tryIt user.castTo()
            itemRepo.saveItems(
                listOf(
                    item.copy(sharedWith = item.sharedWith + listOf(user.data!!.uuid))
                )
            )
        }
    }
}