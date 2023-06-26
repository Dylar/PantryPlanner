package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.ItemRepository
import kotlinx.coroutines.flow.first

class UnfinishChecklistUC(
    private val checkRepo: CheckRepository,
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(checkId: String): Resource<Unit> {
        return tryIt {
            val getResp = checkRepo.getCheckLists(listOf(checkId)).first()
            if (getResp is Resource.Error) {
                return@tryIt getResp.castTo()
            }

            val checklist = getResp.data!!.first()
            val saveChecklist = checklist.copy(finished = false)
            val saveResp = checkRepo.saveChecklist(saveChecklist)
            if (saveResp is Resource.Error) {
                return@tryIt saveResp.castTo()
            }

            val itemsIds = checklist.items.map { it.uuid }
            val itemResp = itemRepo.getAllItems(itemsIds)
            if (itemResp is Resource.Error) {
                return@tryIt itemResp.castTo()
            }
            val items = itemResp.data!!
            items.forEach { item ->
                if (itemsIds.contains(item.uuid)) {
                    val checkItem = checklist.items.first { it.uuid == item.uuid }
                    item.amount -= checkItem.amount
                }
            }
            val saveItems = itemRepo.saveItems(items)
            if (saveItems is Resource.Error) {
                return@tryIt saveItems.castTo()
            }
            Resource.Success()
        }
    }
}