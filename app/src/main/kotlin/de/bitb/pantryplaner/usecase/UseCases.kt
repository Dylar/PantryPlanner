package de.bitb.pantryplaner.usecase

import de.bitb.pantryplaner.usecase.alert.RefreshAlertUC
import de.bitb.pantryplaner.usecase.checklist.*
import de.bitb.pantryplaner.usecase.item.*
import de.bitb.pantryplaner.usecase.user.LoginUC
import de.bitb.pantryplaner.usecase.user.LogoutUC
import de.bitb.pantryplaner.usecase.user.RegisterUC
import de.bitb.pantryplaner.usecase.user.ScanUserUC

data class AlertUseCases(
    val refreshAlertUC: RefreshAlertUC,
)

data class UserUseCases(
    val loadDataUC: LoadDataUC,
    val loginUC: LoginUC,
    val logoutUC: LogoutUC,
    val registerUC: RegisterUC,
    val scanUserUC: ScanUserUC,
)

data class ItemUseCases(
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