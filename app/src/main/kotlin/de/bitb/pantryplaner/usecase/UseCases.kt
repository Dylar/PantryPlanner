package de.bitb.pantryplaner.usecase

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