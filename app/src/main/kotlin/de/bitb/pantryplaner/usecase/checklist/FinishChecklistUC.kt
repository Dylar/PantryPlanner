package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Logger
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
            //TODO on finish select stock
            val checkResp = checkRepo.getCheckLists(listOf(checkId)).first()
            if (checkResp is Resource.Error) return@tryIt checkResp.castTo()

            val checklist = checkResp.data!!.first()
            if (checklist.creator != localDB.getUser()) {
                return@tryIt "Du hast die Liste nicht erstellt".asResourceError()
            }
            if (checklist.items.isEmpty()) {
                return@tryIt "Liste enthält keine Items".asResourceError()
            }

            Logger.justPrint("CHECKLIST:\n$checklist")
            val saveChecklist = checklist.copy(finishedAt = formatDateNow())
            val saveResp = checkRepo.saveChecklist(saveChecklist)
            if (saveResp is Resource.Error) return@tryIt saveResp.castTo()

            val stockResp = stockRepo.getStocks(checklist.creator).first()
            if (stockResp is Resource.Error) return@tryIt stockResp.castTo()

            //TODO thats not right?
            val stock = stockResp.data!!.first { checklist.stock == it.uuid }
            Logger.justPrint("STOCK:\n$stock")
            checklist.items.forEach { checkItem ->
                if (checkItem.checked) {
                    stock.items.first { checkItem.uuid == it.uuid }.amount += checkItem.amount
                }
            }

            stockRepo.saveStocks(listOf(stock))
        }
    }
}