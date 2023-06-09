package de.bitb.pantryplaner.usecase.item

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import kotlinx.coroutines.flow.first

class UncheckAllItemsUC(
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(): Resource<Unit> {
        return tryIt {
            val getItemsResp = itemRepo.getLiveCheckList().first()
            if (getItemsResp is Resource.Error) {
                return@tryIt getItemsResp.castTo()
            }

            val items = getItemsResp.data ?: listOf()
            if(items.isEmpty()){
                return@tryIt Resource.Success()
            }

            val resp = itemRepo.saveItems(items.map { it.copy(checked = false) })
            if (resp is Resource.Error) {
                return@tryIt resp.castTo()
            }
            Resource.Success()
        }
    }
}