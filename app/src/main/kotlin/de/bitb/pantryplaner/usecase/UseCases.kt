package de.bitb.pantryplaner.usecase

import de.bitb.pantryplaner.usecase.item.*

data class ItemUseCases(
    val loadDataUC: LoadDataUC,
    val addItemUC: AddItemUC,
    val removeItemUC: RemoveItemUC,
//    val checkItemUC: checkItemUC,
//    val uncheckAllItemsUC: uncheckAllItemsUC,
)