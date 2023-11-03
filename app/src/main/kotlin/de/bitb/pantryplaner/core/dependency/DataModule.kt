package de.bitb.pantryplaner.core.dependency

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
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
        val fireAuth = FirebaseAuth.getInstance()
        val fireConfig = FirebaseRemoteConfig.getInstance()
        val fireStore = FirebaseFirestore.getInstance()
        val settingsService = FireSettingsService(fireConfig, fireStore)
        val userService = FireUserService(fireStore, fireAuth)
        val itemService = FireItemService(fireStore)
        val checkService = FireCheckService(fireStore)
        val stockItemService = FireStockService(fireStore)

        fireConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(300) // 5 minutes
                .build()
        )
        return PantryRemoteService(
            settingsService,
            userService,
            itemService,
            checkService,
            stockItemService,
        )
    }

}