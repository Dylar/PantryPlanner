package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.StockRepository
import de.bitb.pantryplaner.data.source.LocalDatabase
import kotlinx.coroutines.flow.first

class UnfinishChecklistUC(
    private val localDB: LocalDatabase,
    private val checkRepo: CheckRepository,
    private val stockRepo: StockRepository,
) {
    suspend operator fun invoke(checkId: String): Result<Unit> {
        return tryIt {
            val checkResp = checkRepo.getCheckLists(listOf(checkId)).first()
            if (checkResp is Result.Error) return@tryIt checkResp.castTo()

            val checklist = checkResp.data!!.first()
            if (checklist.creator != localDB.getUser()) {
                return@tryIt "Du hast die Liste nicht erstellt".asError()
            }

            val saveChecklist = checklist.copy(finishedAt = "")
            val saveResp = checkRepo.saveChecklist(saveChecklist)
            if (saveResp is Result.Error) return@tryIt saveResp.castTo()

            val stockResp = stockRepo.getStocks(checklist.creator).first()
            if (stockResp is Result.Error) return@tryIt stockResp.castTo()

            val stock = stockResp.data!!.first { checklist.stock == it.uuid }
            checklist.items.forEach { checkItem ->
                if (checkItem.checked) {
                    val item = stock.items.first { checkItem.uuid == it.uuid }
                    item.amount = (item.amount - checkItem.amount).coerceAtLeast(0.0)
                }
            }

            stockRepo.saveStock(stock)
        }
    }
}