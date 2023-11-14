package de.bitb.pantryplaner.core.dependency

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.bitb.pantryplaner.data.*
import de.bitb.pantryplaner.data.source.*
import de.bitb.pantryplaner.usecase.*
import de.bitb.pantryplaner.usecase.checklist.*
import de.bitb.pantryplaner.usecase.item.*
import de.bitb.pantryplaner.usecase.user.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(
        remoteService: RemoteService,
        localDatabase: LocalDatabase,
    ): SettingsRepository = SettingsRepository(remoteService, localDatabase)

    @Provides
    @Singleton
    fun provideUserRepository(
        remoteService: RemoteService,
        localDatabase: LocalDatabase,
    ): UserRepository = UserRepository(remoteService, localDatabase)

    @Provides
    @Singleton
    fun provideItemRepository(
        remoteService: RemoteService,
        localDatabase: LocalDatabase,
    ): ItemRepository = ItemRepository(remoteService, localDatabase)

    @Provides
    @Singleton
    fun provideCheckRepository(
        remoteService: RemoteService,
        localDatabase: LocalDatabase,
    ): CheckRepository = CheckRepository(remoteService, localDatabase)

    @Provides
    @Singleton
    fun provideRecipeRepository(
        remoteService: RemoteService,
        localDatabase: LocalDatabase,
    ): RecipeRepository = RecipeRepository(remoteService, localDatabase)

    @Provides
    @Singleton
    fun provideStockRepository(
        remoteService: RemoteService,
        localDatabase: LocalDatabase,
    ): StockRepository = StockRepository(remoteService, localDatabase)

}