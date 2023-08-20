package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.formatDateNow
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.ItemRepository
import kotlinx.coroutines.flow.first

class FinishChecklistUC(
    private val checkRepo: CheckRepository,
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(checkId: String): Resource<Unit> {
        return tryIt {
            val checkResp = checkRepo.getCheckLists(listOf(checkId)).first()
            if (checkResp is Resource.Error) return@tryIt checkResp.castTo()

            val checklist = checkResp.data!!.first()
            if (checklist.items.isEmpty()) {
                return@tryIt "Liste enthält keine Items".asResourceError()
            }

            val saveChecklist = checklist.copy(finishedAt = formatDateNow())
            val saveResp = checkRepo.saveChecklist(saveChecklist)
            if (saveResp is Resource.Error) return@tryIt saveResp.castTo()

            //TODO wem wird das hinzugefügt?
            // -> oh gott wem wirds zugeordnet ... dem creator? allen gleich viel?
            // -> shared added garnicht zu bestand? oder nur wenn mans sagt
            // -> "Household"-listen? "None shared"-Listen
            // => Inmom werden wohl den items selber dem bestand hinzugefügt
            // -> aber dann teilen sich ja alle ein Bestand
            // -> shared Bestand? -> oh gott xD
            // => bestand auf user ebene nicht item ebene.... oh gott
            val itemsIds = checklist.items.map { it.uuid }
            val itemResp = itemRepo.getAllItems(itemsIds)
            if (itemResp is Resource.Error) return@tryIt itemResp.castTo()

            val items = itemResp.data!!
            items.forEach { item ->
                if (itemsIds.contains(item.uuid)) {
                    val checkItem = checklist.items.first { it.uuid == item.uuid }
                    item.amount += checkItem.amount
                }
            }
            val saveItems = itemRepo.saveItems(items)
            if (saveItems is Resource.Error) return@tryIt saveItems.castTo()

            Resource.Success()
        }
    }
}