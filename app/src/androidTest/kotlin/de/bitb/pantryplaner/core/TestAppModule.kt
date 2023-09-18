package de.bitb.pantryplaner.core

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import de.bitb.pantryplaner.data.*
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.PantryRemoteService
import de.bitb.pantryplaner.data.source.PreferenceDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.usecase.*
import de.bitb.pantryplaner.usecase.alert.RefreshAlertUC
import de.bitb.pantryplaner.usecase.checklist.*
import de.bitb.pantryplaner.usecase.item.*
import de.bitb.pantryplaner.usecase.stock.AddStockItemUC
import de.bitb.pantryplaner.usecase.stock.DeleteStockItemUC
import de.bitb.pantryplaner.usecase.user.*
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class],
)
object TestAppModule {
    // TESTING
    @Provides
    @Singleton
    fun provideScenarioData(): ScenarioData = ScenarioData()

    // DATABASE
    @Provides
    @Singleton
    fun provideLocalDatabase(app: Application): LocalDatabase {
        val pref = app.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return PreferenceDatabase(pref) // TODO make real DB
    }

    @Provides
    @Singleton
    fun provideRemoteDatabase(): RemoteService = mockk<PantryRemoteService>(relaxed = true)

    // REPO
    @Provides
    @Singleton
    fun provideSettingsRepository(
        remoteService: RemoteService,
    ): SettingsRepository = SettingsRepositoryImpl(remoteService)

    @Provides
    @Singleton
    fun provideUserRepository(
        remoteService: RemoteService,
        localDatabase: LocalDatabase,
    ): UserRepository = UserRepositoryImpl(remoteService, localDatabase)

    @Provides
    @Singleton
    fun provideItemRepository(
        remoteService: RemoteService,
        localDatabase: LocalDatabase,
    ): ItemRepository = ItemRepositoryImpl(remoteService, localDatabase)

    @Provides
    @Singleton
    fun provideCheckRepository(
        remoteService: RemoteService,
        localDatabase: LocalDatabase,
    ): CheckRepository = CheckRepositoryImpl(remoteService, localDatabase)

    @Provides
    @Singleton
    fun provideStockRepository(
        remoteService: RemoteService,
        localDatabase: LocalDatabase,
    ): StockRepository = StockRepository(remoteService, localDatabase)

    //USE CASES
    @Provides
    @Singleton
    fun provideUserUseCases(
        userRepo: UserRepository,
    ): UserUseCases {
        return UserUseCases(
            loadDataUC = LoadDataUC(userRepo),
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
        userRepo: UserRepository,
        itemRepo: ItemRepository,
        stockRepo: StockRepository,
    ): ItemUseCases {
        return ItemUseCases(
            createItemUC = CreateItemUC(userRepo, itemRepo),
            deleteItemUC = DeleteItemUC(userRepo, itemRepo),
            editItemUC = EditItemUC(itemRepo, stockRepo),
            editCategoryUC = EditCategoryUC(itemRepo, stockRepo),
            uncheckAllItemsUC = UncheckAllItemsUC(itemRepo),
        )
    }

    @Provides
    @Singleton
    fun provideStockUseCases(
        stockRepo: StockRepository,
    ): StockUseCases {
        return StockUseCases(
            addStockItemUC = AddStockItemUC(stockRepo),
            deleteStockItemUC = DeleteStockItemUC(stockRepo),
        )
    }

    @Provides
    @Singleton
    fun provideCheckListUseCases(
        localDB: LocalDatabase,
        userRepo: UserRepository,
        checkRepo: CheckRepository,
        stockRepo: StockRepository,
    ): ChecklistUseCases {
        return ChecklistUseCases(
            createChecklistUC = CreateChecklistUC(userRepo, checkRepo),
            deleteChecklistUC = DeleteChecklistUC(userRepo, checkRepo),
            addItemsUC = AddItemsUC(checkRepo),
            removeItemsUC = RemoveItemsUC(checkRepo),
            checkItemUC = CheckItemUC(checkRepo),
            finishChecklistUC = FinishChecklistUC(localDB, checkRepo, stockRepo),
            unfinishChecklistUC = UnfinishChecklistUC(localDB, checkRepo, stockRepo),
            setItemAmountUC = SetItemAmountUC(checkRepo),
            setSharedWithUC = SetSharedWithUC(checkRepo),
        )
    }

}