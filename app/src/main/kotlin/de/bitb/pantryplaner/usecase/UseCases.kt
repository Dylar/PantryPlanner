package de.bitb.pantryplaner.usecase

import de.bitb.pantryplaner.usecase.alert.ItemAlertUC
import de.bitb.pantryplaner.usecase.checklist.*
import de.bitb.pantryplaner.usecase.item.*

data class AlertUseCases(
    val itemAlertUC: ItemAlertUC,
)

data class ItemUseCases(
    val loadDataUC: LoadDataUC,
    val addItemUC: AddItemUC,
    val removeItemUC: RemoveItemUC,
    val editItemUC: EditItemUC,
    val editCategoryUC: EditCategoryUC,
    val uncheckAllItemsUC: UncheckAllItemsUC,
)

data class ChecklistUseCases(
    val addChecklistUC: AddChecklistUC,
    val removeChecklistUC: RemoveChecklistUC,
    val addItemsToChecklistUC: AddItemsToChecklistUC,
    val removeItemsFromChecklistUC: RemoveItemsFromChecklistUC,
    val checkItemUC: CheckItemUC,
    val finishChecklistUC: FinishChecklistUC,
    val unfinishChecklistUC: UnfinishChecklistUC,
    val setItemAmountUC: SetChecklistItemAmountUC,
)