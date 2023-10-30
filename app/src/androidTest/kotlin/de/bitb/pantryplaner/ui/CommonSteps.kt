package de.bitb.pantryplaner.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.fragment.NavHostFragment
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.SNACKBARS_ENABLED
import io.cucumber.java.en.Then

@HiltAndroidTest
class CommonSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("On Back")
    fun onBack() {
        scenarioData.scenario?.onActivity { activity ->
            val fragManger = activity.supportFragmentManager
            val navFrag = (fragManger.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
            navFrag.navController.popBackStack()
        }
        waitForIdle()
    }

    @OptIn(ExperimentalTestApi::class)
    @Then("SnackBar shown: {string}")
    fun snackBarShown(message: String) {
        waitUntilAtLeastOneExists(hasText(message), 15000)
        onNodeWithText(message).assertIsDisplayed()
    }

    @Then("Disable SnackBars")
    fun disableSnackBars() {
        SNACKBARS_ENABLED = false
    }

    @OptIn(ExperimentalTestApi::class)
    @Then("Wait until SnackBar {string} vanish")
    fun waitUntilSnackBarVanish(message: String) {
        waitUntilAtLeastOneExists(hasText(message), 15000)
        onNodeWithText(message).assertIsDisplayed()
        waitUntilDoesNotExist(hasText(message), 15000)
        onNodeWithText(message).assertDoesNotExist()
    }

    @Then("Tap on allow permission")
    fun tapOnAllowPermission() {
        performTapOnAllowPermission()
    }
}

fun ComposeTestRule.performTapOnAllowPermission() {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    val allowButton = device.findObject(UiSelector().text("WHILE USING THE APP"))
    if (allowButton.exists()) allowButton.click()
    waitForIdle()
}