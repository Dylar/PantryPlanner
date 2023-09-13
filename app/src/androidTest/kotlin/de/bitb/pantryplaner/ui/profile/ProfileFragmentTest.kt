//package de.bitb.pantryplaner.ui.profile
//
//import androidx.compose.ui.test.*
//import androidx.compose.ui.test.junit4.createAndroidComposeRule
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.filters.MediumTest
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import dagger.hilt.android.testing.UninstallModules
//import de.bitb.pantryplaner.core.AppModule
//import de.bitb.pantryplaner.core.MainActivity
//import de.bitb.pantryplaner.core.TestAppModule
//import de.bitb.pantryplaner.data.source.LocalDatabase
//import de.bitb.pantryplaner.data.source.RemoteService
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.runTest
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import javax.inject.Inject
//
//@MediumTest
//@HiltAndroidTest
//@RunWith(AndroidJUnit4::class)
//@UninstallModules(AppModule::class)
//@OptIn(ExperimentalCoroutinesApi::class)
//class ProfileFragmentTest {
//
//    @get:Rule(order = 0)
//    val hiltRule = HiltAndroidRule(this)
//
//    @get:Rule(order = 1)
//    val composeRule = createAndroidComposeRule<MainActivity>()
//
//    @Inject
//    lateinit var remoteService: RemoteService
//
//    @Inject
//    lateinit var localDatabase: LocalDatabase
//
//    @Before
//    fun setUp() {
//        hiltRule.inject()
//    }
//
////    @Test
////    fun render_profileFragment() = runTest {
////        composeRule.apply {
////            val user = buildUser()
////            remoteService.mockWholeService(user)
////
////            navigateTo(TestNavigation.Profile(user))
////            waitForIdle()
////            onNodeWithTag(ProfileFragment.APPBAR_TAG)
////                .assertIsDisplayed()
////                .onChildren()
////                .assertAny(hasText(getString(R.string.profile_title)))
////            onNodeWithTag(ProfileFragment.SETTINGS_BUTTON_TAG)
////                .assertIsDisplayed()
////            onNodeWithTag(ProfileFragment.QR_TAG)
////                .assertIsDisplayed()
////            onNodeWithTag(ProfileFragment.QR_INFO_TAG)
////                .assertIsDisplayed()
////                .onChildren()
////                .assertAny(hasText(getString(R.string.profile_qr_info)))
////        }
////    }
//}
