package de.bitb.pantryplaner.core.dependency

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

const val PREF_NAME = "buddy_pref"

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideLocalDatabase(app: Application): LocalDatabase {
        val pref = app.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return PreferenceDatabase(pref) // TODO make real DB
    }

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
        val stockItemService = FireStockService(fireData)

        return PantryRemoteService(
            settingsService,
            userService,
            itemService,
            checkService,
            stockItemService,
        )
    }

}