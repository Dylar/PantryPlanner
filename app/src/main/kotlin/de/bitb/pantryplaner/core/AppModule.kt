package de.bitb.pantryplaner.core

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.bitb.pantryplaner.data.*
import de.bitb.pantryplaner.data.source.*
import de.bitb.pantryplaner.usecase.AlertUseCases
import de.bitb.pantryplaner.usecase.ChecklistUseCases
import de.bitb.pantryplaner.usecase.ItemUseCases
import de.bitb.pantryplaner.usecase.UserUseCases
import de.bitb.pantryplaner.usecase.alert.RefreshAlertUC
import de.bitb.pantryplaner.usecase.checklist.*
import de.bitb.pantryplaner.usecase.item.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // DATABASE
    @Provides
    @Singleton
    fun provideRemoteDatabase(app: Application): RemoteService {
        FirebaseApp.initializeApp(app)
        val fireData = FirebaseFirestore.getInstance()
        val fireAuth = FirebaseAuth.getInstance()
        val settingsService = FireSettingsService(fireData)
        val userService = FireUserService(fireData, fireAuth)
        val itemService = FireItemService(fireData)
        val checkService = FireCheckService(fireData)

        return PantryRemoteService(settingsService, userService, itemService, checkService)
    }

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
    ): UserRepository = UserRepositoryImpl(remoteService)

    @Provides
    @Singleton
    fun provideItemRepository(
        remoteService: RemoteService,
    ): ItemRepository = ItemRepositoryImpl(remoteService)

    @Provides
    @Singleton
    fun provideCheckRepository(
        remoteService: RemoteService,
    ): CheckRepository = CheckRepositoryImpl(remoteService)

    //USE CASES
    @Provides
    @Singleton
    fun provideUserUseCases(
        userRepo: UserRepository,
    ): UserUseCases {
        return UserUseCases(
            loadDataUC = LoadDataUC(userRepo),
        )
    }

    @Provides
    @Singleton
    fun provideAlertUseCases(
        checkRepo: CheckRepository,
        itemRepo: ItemRepository,
    ): AlertUseCases {
        return AlertUseCases(
            refreshAlertUC = RefreshAlertUC(checkRepo, itemRepo),
        )
    }

    @Provides
    @Singleton
    fun provideItemUseCases(
        itemRepo: ItemRepository,
    ): ItemUseCases {
        return ItemUseCases(
            addItemUC = AddItemUC(itemRepo),
            removeItemUC = RemoveItemUC(itemRepo),
            editItemUC = EditItemUC(itemRepo),
            editCategoryUC = EditCategoryUC(itemRepo),
            uncheckAllItemsUC = UncheckAllItemsUC(itemRepo),
        )
    }

    @Provides
    @Singleton
    fun provideCheckListUseCases(
        checkRepo: CheckRepository,
        itemRepo: ItemRepository,
    ): ChecklistUseCases {
        return ChecklistUseCases(
            addChecklistUC = AddChecklistUC(checkRepo),
            removeChecklistUC = RemoveChecklistUC(checkRepo),
            addItemsToChecklistUC = AddItemsToChecklistUC(checkRepo),
            removeItemsFromChecklistUC = RemoveItemsFromChecklistUC(checkRepo),
            checkItemUC = CheckItemUC(checkRepo),
            finishChecklistUC = FinishChecklistUC(checkRepo, itemRepo),
            unfinishChecklistUC = UnfinishChecklistUC(checkRepo, itemRepo),
            setItemAmountUC = SetChecklistItemAmountUC(checkRepo)
        )
    }

}