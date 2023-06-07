package de.bitb.pantryplaner.core

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.bitb.pantryplaner.data.*
import de.bitb.pantryplaner.data.source.*
import de.bitb.pantryplaner.usecase.ItemUseCases
import de.bitb.pantryplaner.usecase.user.*
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
        val fireService = FirestoreService(fireData)

        return PantryRemoteService(fireService)
    }

    // REPO
    @Provides
    @Singleton
    fun provideItemRepository(
        remoteService: RemoteService,
    ): ItemRepository = PantryRepositoryImpl(remoteService)

    //USE CASES
    @Provides
    @Singleton
    fun provideItemUseCases(
        itemRepo: ItemRepository,
    ): ItemUseCases {
        return ItemUseCases(
            loadDataUC = LoadDataUC( itemRepo),
        )
    }

}

//@Module
//@InstallIn(FragmentComponent::class)
//object FragmentModule {
//
//}