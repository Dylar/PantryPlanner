package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import kotlinx.coroutines.flow.first

class SetChecklistItemAmountUC(
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(
        checkId: String,
        itemId: String,
        amount: String,
    ): Resource<Boolean> {
        return tryIt(
            onError = {
                when (it) {
                    is NumberFormatException -> {
                        if (amount.isEmpty()) {
                            Resource.Success()
                        } else "Not a number error".asResourceError()
                    }
                    else -> Resource.Error(it, false)
                }
            },
            onTry = {
                val amountDouble = amount.replace(",", ".").toDouble()

                val getResp = checkRepo.getCheckList(checkId).first()
                if (getResp is Resource.Error) return@tryIt getResp.castTo(false)

                val checklist = getResp.data!!
                val item = checklist.items.first { it.uuid == itemId }
                item.amount = amountDouble
                val saveResp = checkRepo.saveChecklist(checklist)
                if (saveResp is Resource.Error) return@tryIt saveResp.castTo(false)

                Resource.Success(true)
            },
        )
    }
}