package de.bitb.pantryplaner.usecase

import de.bitb.pantryplaner.usecase.alert.RefreshAlertUC
import de.bitb.pantryplaner.usecase.checklist.AddChecklistItemsUC
import de.bitb.pantryplaner.usecase.checklist.CheckItemUC
import de.bitb.pantryplaner.usecase.checklist.CreateChecklistUC
import de.bitb.pantryplaner.usecase.checklist.DeleteChecklistUC
import de.bitb.pantryplaner.usecase.checklist.FinishChecklistUC
import de.bitb.pantryplaner.usecase.checklist.RemoveChecklistItemsUC
import de.bitb.pantryplaner.usecase.checklist.SaveChecklistUC
import de.bitb.pantryplaner.usecase.checklist.SetChecklistItemAmountUC
import de.bitb.pantryplaner.usecase.checklist.SetChecklistSharedWithUC
import de.bitb.pantryplaner.usecase.checklist.SetStockWithUC
import de.bitb.pantryplaner.usecase.checklist.UnfinishChecklistUC
import de.bitb.pantryplaner.usecase.item.CreateItemUC
import de.bitb.pantryplaner.usecase.item.DeleteItemUC
import de.bitb.pantryplaner.usecase.item.EditCategoryUC
import de.bitb.pantryplaner.usecase.item.EditItemUC
import de.bitb.pantryplaner.usecase.item.ShareItemUC
import de.bitb.pantryplaner.usecase.recipe.AddRecipeItemsUC
import de.bitb.pantryplaner.usecase.recipe.CookRecipeUC
import de.bitb.pantryplaner.usecase.recipe.CreateRecipeUC
import de.bitb.pantryplaner.usecase.recipe.DeleteRecipeUC
import de.bitb.pantryplaner.usecase.recipe.IsCookableUC
import de.bitb.pantryplaner.usecase.recipe.RemoveRecipeItemsUC
import de.bitb.pantryplaner.usecase.recipe.SaveRecipeUC
import de.bitb.pantryplaner.usecase.recipe.SetRecipeItemAmountUC
import de.bitb.pantryplaner.usecase.recipe.SetRecipeSharedWithUC
import de.bitb.pantryplaner.usecase.stock.AddEditStockItemUC
import de.bitb.pantryplaner.usecase.stock.AddStockUC
import de.bitb.pantryplaner.usecase.stock.DeleteStockUC
import de.bitb.pantryplaner.usecase.stock.EditStockUC
import de.bitb.pantryplaner.usecase.user.ConnectUserUC
import de.bitb.pantryplaner.usecase.user.DisconnectUserUC
import de.bitb.pantryplaner.usecase.user.LoadDataUC
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
    val addItemsUC: AddChecklistItemsUC,
    val removeItemsUC: RemoveChecklistItemsUC,
    val checkItemUC: CheckItemUC,
    val finishChecklistUC: FinishChecklistUC,
    val unfinishChecklistUC: UnfinishChecklistUC,
    val setItemAmountUC: SetChecklistItemAmountUC, //TODO make one single edit UC
    val setSharedWithUC: SetChecklistSharedWithUC,
    val setStockWithUC: SetStockWithUC,
    val saveChecklistUC: SaveChecklistUC,
)

data class RecipeUseCases(
    val createRecipeUC: CreateRecipeUC,
    val deleteRecipeUC: DeleteRecipeUC,
    val addItemsUC: AddRecipeItemsUC,
    val removeItemsUC: RemoveRecipeItemsUC,
    val setItemAmountUC: SetRecipeItemAmountUC,
    val setSharedWithUC: SetRecipeSharedWithUC,
    val saveRecipeUC: SaveRecipeUC,
    val isCookableUC: IsCookableUC,
    val cookRecipeUC: CookRecipeUC,
)