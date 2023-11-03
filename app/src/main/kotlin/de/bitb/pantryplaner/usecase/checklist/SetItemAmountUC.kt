package de.bitb.pantryplaner.usecase.checklist

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.CheckRepository
import kotlinx.coroutines.flow.first

class SetItemAmountUC(
    private val checkRepo: CheckRepository,
) {
    suspend operator fun invoke(
        checkId: String,
        itemId: String,
        amount: String,
    ): Result<Boolean> {
        return tryIt(
            onError = {
                when (it) {
                    is NumberFormatException -> {
                        if (amount.isEmpty()) Result.Success()
                        else "Not a number error".asError()
                    }

                    else -> Result.Error(it, false)
                }
            },
            onTry = {
                val amountDouble = amount.replace(",", ".").toDouble()

                val getResp = checkRepo.getCheckList(checkId).first()
                if (getResp is Result.Error) return@tryIt getResp.castTo(false)

                val checklist = getResp.data!!
                val item = checklist.items.first { it.uuid == itemId }
                item.amount = amountDouble
                val saveResp = checkRepo.saveChecklist(checklist)
                if (saveResp is Result.Error) return@tryIt saveResp.castTo(false)

                Result.Success(true)
            },
        )
    }
}