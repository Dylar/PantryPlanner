package de.bitb.pantryplaner.usecase

import de.bitb.pantryplaner.usecase.alert.RefreshAlertUC
import de.bitb.pantryplaner.usecase.checklist.*
import de.bitb.pantryplaner.usecase.item.*
import de.bitb.pantryplaner.usecase.stock.AddStockItemUC
import de.bitb.pantryplaner.usecase.stock.DeleteStockItemUC
import de.bitb.pantryplaner.usecase.user.*

data class AlertUseCases(
    val refreshAlertUC: RefreshAlertUC,
)

data class UserUseCases(
    val loadDataUC: LoadDataUC,
    val loginUC: LoginUC,
    val logoutUC: LogoutUC,
    val registerUC: RegisterUC,
    val connectUserUC: ConnectUserUC,
    val disconnectUserUC: DisconnectUserUC,
)

data class ItemUseCases(
    val createItemUC: CreateItemUC,
    val deleteItemUC: DeleteItemUC,
    val editItemUC: EditItemUC,
    val editCategoryUC: EditCategoryUC,
    val uncheckAllItemsUC: UncheckAllItemsUC,
)

data class StockUseCases(
    val addStockItemUC: AddStockItemUC,
    val deleteStockItemUC: DeleteStockItemUC,
)

data class ChecklistUseCases(
    val createChecklistUC: CreateChecklistUC,
    val deleteChecklistUC: DeleteChecklistUC,
    val addItemsUC: AddItemsUC,
    val removeItemsUC: RemoveItemsUC,
    val checkItemUC: CheckItemUC,
    val finishChecklistUC: FinishChecklistUC,
    val unfinishChecklistUC: UnfinishChecklistUC,
    val setItemAmountUC: SetItemAmountUC, //TODO make one single edit UC
    val setSharedWithUC: SetSharedWithUC,
)