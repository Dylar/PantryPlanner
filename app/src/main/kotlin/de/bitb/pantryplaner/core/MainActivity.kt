package de.bitb.pantryplaner.core

import android.app.Application
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Logger
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.SettingsRepository
import de.bitb.pantryplaner.data.model.Settings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltAndroidApp
class PantryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        AlertManager.setRepeatingAlarm(this)
    }
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsRepo: SettingsRepository
    fun settingsFlow(): Flow<Resource<Settings>> = settingsRepo.getSettings()

//    val navHostFragment by lazy {
//        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setupNavigation()
    }

//    override fun onSupportNavigateUp(): Boolean {
//        Logger.printLog("" to "onSupportNavigateUp")
//        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
//    }

//    private fun setupNavigation() {
//        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                Logger.printLog("" to "handleOnBackPressed")
//                navHostFragment.navController.navigateUp()
//            }
//        })
//    }
}