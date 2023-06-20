package de.bitb.pantryplaner.usecase

import de.bitb.pantryplaner.usecase.checklist.AddChecklistUC
import de.bitb.pantryplaner.usecase.checklist.RemoveChecklistUC
import de.bitb.pantryplaner.usecase.item.*

data class ItemUseCases(
    val loadDataUC: LoadDataUC,
    val addItemUC: AddItemUC,
    val removeItemUC: RemoveItemUC,
    val checkItemUC: CheckItemUC,
    val editItemUC: EditItemUC,
    val editCategoryUC: EditCategoryUC,
    val uncheckAllItemsUC: UncheckAllItemsUC,
)

data class ChecklistUseCases(
    val addChecklistUC: AddChecklistUC,
    val removeChecklistUC: RemoveChecklistUC,

    )