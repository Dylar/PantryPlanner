package de.bitb.pantryplaner.core

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import de.bitb.pantryplaner.core.dependency.DataModule
import de.bitb.pantryplaner.core.dependency.PREF_NAME
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.PantryRemoteService
import de.bitb.pantryplaner.data.source.PreferenceDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import de.bitb.pantryplaner.test.ScenarioData
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class],
)
object TestDataModule {

    @Provides
    @Singleton
    fun provideScenarioData(): ScenarioData = ScenarioData()

    @Provides
    @Singleton
    fun provideLocalDatabase(app: Application): LocalDatabase {
        val pref = app.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
        return PreferenceDatabase(pref) // TODO make real DB
    }

    @Provides
    @Singleton
    fun provideRemoteDatabase(): RemoteService = mockk<PantryRemoteService>(relaxed = true)

}