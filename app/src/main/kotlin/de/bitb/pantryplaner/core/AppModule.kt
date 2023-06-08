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
import de.bitb.pantryplaner.usecase.ItemUseCases
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
        val fireService = FirestoreService(fireData, fireAuth)

        return PantryRemoteService(fireService)
    }

    // REPO
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

    //USE CASES
    @Provides
    @Singleton
    fun provideItemUseCases(
        userRepo: UserRepository,
        itemRepo: ItemRepository,
    ): ItemUseCases {
        return ItemUseCases(
            loadDataUC = LoadDataUC(userRepo),
            addItemUC = AddItemUC(itemRepo),
            removeItemUC = RemoveItemUC(itemRepo)
        )
    }

}

//@Module
//@InstallIn(FragmentComponent::class)
//object FragmentModule {
//
//}