package de.bitb.pantryplaner.core.dependency

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.bitb.pantryplaner.data.*
import de.bitb.pantryplaner.data.source.*
import de.bitb.pantryplaner.usecase.*
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
import de.bitb.pantryplaner.usecase.item.*
import de.bitb.pantryplaner.usecase.recipe.*
import de.bitb.pantryplaner.usecase.stock.AddEditStockItemUC
import de.bitb.pantryplaner.usecase.stock.AddStockUC
import de.bitb.pantryplaner.usecase.stock.DeleteStockUC
import de.bitb.pantryplaner.usecase.stock.EditStockUC
import de.bitb.pantryplaner.usecase.user.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UsecaseModule {

    @Provides
    @Singleton
    fun provideUserUseCases(
        settingsRepo: SettingsRepository,
        userRepo: UserRepository,
    ): UserUseCases {
        return UserUseCases(
            loadDataUC = LoadDataUC(settingsRepo, userRepo),
            loginUC = LoginUC(userRepo),
            logoutUC = LogoutUC(userRepo),
            registerUC = RegisterUC(userRepo),
            connectUserUC = ConnectUserUC(userRepo),
            disconnectUserUC = DisconnectUserUC(userRepo)
        )
    }

    @Provides
    @Singleton
    fun provideAlertUseCases(
        settingsRepo: SettingsRepository,
        checkRepo: CheckRepository,
        stockRepo: StockRepository,
    ): AlertUseCases {
        return AlertUseCases(
            refreshAlertUC = RefreshAlertUC(settingsRepo, checkRepo, stockRepo),
        )
    }

    @Provides
    @Singleton
    fun provideItemUseCases(
        settingsRepo: SettingsRepository,
        userRepo: UserRepository,
        itemRepo: ItemRepository,
    ): ItemUseCases {
        return ItemUseCases(
            createItemUC = CreateItemUC(userRepo, itemRepo),
            deleteItemUC = DeleteItemUC(userRepo, itemRepo),
            editItemUC = EditItemUC(userRepo, itemRepo),
            editCategoryUC = EditCategoryUC(settingsRepo, itemRepo),
            shareItemUC = ShareItemUC(userRepo, itemRepo),
        )
    }

    @Provides
    @Singleton
    fun provideStockUseCases(
        userRepo: UserRepository,
        stockRepo: StockRepository,
    ): StockUseCases {
        return StockUseCases(
            addStockUC = AddStockUC(stockRepo),
            deleteStockUC = DeleteStockUC(userRepo, stockRepo),
            editStockUC = EditStockUC(userRepo, stockRepo),
            addEditStockItemUC = AddEditStockItemUC(userRepo, stockRepo),
        )
    }

    @Provides
    @Singleton
    fun provideChecklistUseCases(
        localDB: LocalDatabase,
        userRepo: UserRepository,
        checkRepo: CheckRepository,
        stockRepo: StockRepository,
    ): ChecklistUseCases {
        return ChecklistUseCases(
            createChecklistUC = CreateChecklistUC(userRepo, checkRepo),
            deleteChecklistUC = DeleteChecklistUC(userRepo, checkRepo),
            addItemsUC = AddChecklistItemsUC(checkRepo),
            removeItemsUC = RemoveChecklistItemsUC(checkRepo),
            checkItemUC = CheckItemUC(checkRepo),
            finishChecklistUC = FinishChecklistUC(localDB, checkRepo, stockRepo),
            unfinishChecklistUC = UnfinishChecklistUC(localDB, checkRepo, stockRepo),
            setItemAmountUC = SetChecklistItemAmountUC(checkRepo),
            setSharedWithUC = SetChecklistSharedWithUC(userRepo, checkRepo),
            setStockWithUC = SetStockWithUC(userRepo, checkRepo),
            saveChecklistUC = SaveChecklistUC(userRepo, checkRepo)
        )
    }

    @Provides
    @Singleton
    fun provideRecipeUseCases(
        userRepo: UserRepository,
        checkRepo: CheckRepository,
        recipeRepo: RecipeRepository,
    ): RecipeUseCases {
        return RecipeUseCases(
            createRecipeUC = CreateRecipeUC(userRepo, recipeRepo),
            deleteRecipeUC = DeleteRecipeUC(userRepo, recipeRepo),
            addItemsUC = AddRecipeItemsUC(checkRepo),
            removeItemsUC = RemoveRecipeItemsUC(checkRepo),
            setItemAmountUC = SetRecipeItemAmountUC(checkRepo),
            setSharedWithUC = SetRecipeSharedWithUC(userRepo, checkRepo),
            saveRecipeUC = SaveRecipeUC(userRepo, recipeRepo)
        )
    }

}