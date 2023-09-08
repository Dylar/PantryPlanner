package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.formatDateNow
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.source.LocalDatabase
import kotlinx.coroutines.flow.first

class FinishChecklistUC(
    private val localDB: LocalDatabase,
    private val checkRepo: CheckRepository,
    private val stockRepo: StockRepository,
) {
    suspend operator fun invoke(checkId: String): Resource<Unit> {
        return tryIt {
            val checkResp = checkRepo.getCheckLists(listOf(checkId)).first()
            if (checkResp is Resource.Error) return@tryIt checkResp.castTo()

            val checklist = checkResp.data!!.first()
            if (checklist.creator != localDB.getUser()) {
                return@tryIt "Du hast die Liste nicht erstellt".asResourceError()
            }
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
            // -> bestand obj mit sharedWith

            // TODO share with location not user
            val stockResp = stockRepo.getStockItems(checklist.creator).first()
            if (stockResp is Resource.Error) return@tryIt stockResp.castTo()

            val stockItems = stockResp.data!!
            checklist.items.forEach { checkItem ->
                if (checkItem.checked) {
                    stockItems[checkItem.uuid]!!.amount += checkItem.amount
                }
            }

            stockRepo.saveItems(stockItems.values.toList())
        }
    }
}