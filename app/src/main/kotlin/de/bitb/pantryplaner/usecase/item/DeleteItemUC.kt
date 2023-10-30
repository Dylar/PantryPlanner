package de.bitb.pantryplaner.usecase.item

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.UserRepository
import de.bitb.pantryplaner.data.model.Item
import kotlinx.coroutines.flow.first

class DeleteItemUC(
    private val userRepo: UserRepository,
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(item: Item): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                val userResp = userRepo.getUser().first()
                if (userResp is Resource.Error) return@tryIt userResp.castTo(false)

                val user = userResp.data?.uuid
                if (user == item.creator) { //TODO what if other user using it? -> unlucky xD
                    itemRepo.deleteItem(item)
                } else {
                    val newList = item.sharedWith.subtract(setOf(user!!))
                    itemRepo.saveItems(listOf(item.copy(sharedWith = newList.toList())))
                    Resource.Success(true)
                }
            },
        )
    }
}