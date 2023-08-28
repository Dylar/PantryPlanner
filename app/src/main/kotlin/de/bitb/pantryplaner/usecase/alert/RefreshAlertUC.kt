package de.bitb.pantryplaner.usecase.alert

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
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
    suspend operator fun invoke(): Resource<Boolean> {
        return tryIt(
            onError = { it.asResourceError(false) },
            onTry = {
                val settings = settingsRepo.getSettings().first()
                if (settings is Resource.Error) return@tryIt settings.castTo(false)
                if (settings.data?.refreshAlert != true) return@tryIt Resource.Success(false)

                val checkResp = checkRepo.getCheckLists().first()
                if (checkResp is Resource.Error) return@tryIt checkResp.castTo(false)

                val stockResp = stockRepo.getStockItems().first()
                if (stockResp is Resource.Error) return@tryIt stockResp.castTo(false)

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
                        val stockItems = stockResp.data!!
                        stockItems.values
                            .filter { stockItem ->
                                !unfinishedItems.contains(stockItem.uuid) &&
                                        stockItem.isAlertable(finishDay)
                            }
                            .map { it.uuid }
                    }
                    .flatten()
                    .toSet()
                    .let { Resource.Success(it.isNotEmpty()) }
            }
        )
    }
}