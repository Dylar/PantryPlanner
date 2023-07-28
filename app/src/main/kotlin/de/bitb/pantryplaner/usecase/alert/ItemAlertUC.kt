package de.bitb.pantryplaner.usecase.alert

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import de.bitb.pantryplaner.data.ItemRepository
import kotlinx.coroutines.flow.first

class ItemAlertUC(
    private val checkRepo: CheckRepository,
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(): Resource<Boolean> {
        return tryIt(
            onError = { it.asResourceError(false) },
            onTry = {
                //TODO load settings to disable
                val getResp = checkRepo.getCheckLists().first()
                if (getResp is Resource.Error) {
                    return@tryIt getResp.castTo(false)
                }

                val items = getResp.data!!
                    .filter { it.finished }
                    .map { check ->
                        val ids = check.items.map { it.uuid }
                        val itemResp = itemRepo.getAllItems(ids)
                        if (itemResp is Resource.Error) {
                            return@tryIt itemResp.castTo(false)
                        }

                        val finishDay = check.finishDate.toLocalDate()
                        itemResp.data!!
                            .filter { !it.isBest(finishDay) || it.remindIt(finishDay) }
                            .map { it.uuid }
                    }
                    .flatten()
                    .toSet()

                Resource.Success(items.isNotEmpty())
            }
        )
    }
}