package de.bitb.pantryplaner.usecase

import de.bitb.pantryplaner.usecase.alert.RefreshAlertUC
import de.bitb.pantryplaner.usecase.checklist.AddItemsUC
import de.bitb.pantryplaner.usecase.checklist.CheckItemUC
import de.bitb.pantryplaner.usecase.checklist.CreateChecklistUC
import de.bitb.pantryplaner.usecase.checklist.DeleteChecklistUC
import de.bitb.pantryplaner.usecase.checklist.FinishChecklistUC
import de.bitb.pantryplaner.usecase.checklist.RemoveItemsUC
import de.bitb.pantryplaner.usecase.checklist.SaveChecklistUC
import de.bitb.pantryplaner.usecase.checklist.SetItemAmountUC
import de.bitb.pantryplaner.usecase.checklist.SetSharedWithUC
import de.bitb.pantryplaner.usecase.checklist.SetStockWithUC
import de.bitb.pantryplaner.usecase.checklist.UnfinishChecklistUC
import de.bitb.pantryplaner.usecase.item.CreateItemUC
import de.bitb.pantryplaner.usecase.item.DeleteItemUC
import de.bitb.pantryplaner.usecase.item.EditCategoryUC
import de.bitb.pantryplaner.usecase.item.EditItemUC
import de.bitb.pantryplaner.usecase.user.LoadDataUC
import de.bitb.pantryplaner.usecase.item.ShareItemUC
import de.bitb.pantryplaner.usecase.item.UncheckAllItemsUC
import de.bitb.pantryplaner.usecase.stock.AddEditStockItemUC
import de.bitb.pantryplaner.usecase.stock.AddStockUC
import de.bitb.pantryplaner.usecase.stock.DeleteStockUC
import de.bitb.pantryplaner.usecase.stock.EditStockUC
import de.bitb.pantryplaner.usecase.user.ConnectUserUC
import de.bitb.pantryplaner.usecase.user.DisconnectUserUC
import de.bitb.pantryplaner.usecase.user.LoginUC
import de.bitb.pantryplaner.usecase.user.LogoutUC
import de.bitb.pantryplaner.usecase.user.RegisterUC

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
    val shareItemUC: ShareItemUC,
    val uncheckAllItemsUC: UncheckAllItemsUC,
)

data class StockUseCases(
    val addStockUC: AddStockUC,
    val deleteStockUC: DeleteStockUC,
    val editStockUC: EditStockUC,
    val addEditStockItemUC: AddEditStockItemUC,
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
    val setStockWithUC: SetStockWithUC,
    val saveChecklistUC: SaveChecklistUC,
)