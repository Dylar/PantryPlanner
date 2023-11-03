package de.bitb.pantryplaner.usecase.alert

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.SettingsRepository
import de.bitb.pantryplaner.data.StockRepository
import kotlinx.coroutines.flow.first

class RefreshAlertUC(
    private val settingsRepo: SettingsRepository,
    private val checkRepo: CheckRepository,
    private val stockRepo: StockRepository,
) {
    suspend operator fun invoke(): Result<Boolean> {
        return tryIt(
            onError = { it.asError(false) },
            onTry = {
                val settings = settingsRepo.getSettings().first()
                if (settings is Result.Error) return@tryIt settings.castTo(false)
                if (settings.data?.refreshAlert != true) return@tryIt Result.Success(false)

                val checkResp = checkRepo.getCheckLists().first()
                if (checkResp is Result.Error) return@tryIt checkResp.castTo(false)

                val stockResp = stockRepo.getStocks().first()
                if (stockResp is Result.Error) return@tryIt stockResp.castTo(false)

                val allLists = checkResp.data!!
                val unfinishedItems = allLists
                    .asSequence()
                    .filter { !it.finished }
                    .map { it.items }
                    .flatten()
                    .toSet()
                    .map { it.uuid }

                allLists
                    .filter { it.finished }
                    .map { check ->
                        val finishDay = check.finishDate.toLocalDate()
                        //TODO do for each stock? whatever... haha you need to fix everything with refresh/alert
                        val stockItems = stockResp.data!!.first().items.associateBy { it.uuid }
                        stockItems.values
                            .filter { stockItem ->
                                !unfinishedItems.contains(stockItem.uuid) &&
                                        stockItem.isAlertable(finishDay)
                            }
                            .map { it.uuid }
                    }
                    .flatten()
                    .toSet()
                    .let { Result.Success(it.isNotEmpty()) }
            }
        )
    }
}