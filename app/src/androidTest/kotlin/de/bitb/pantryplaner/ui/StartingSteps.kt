package de.bitb.pantryplaner.ui

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.core.app.launchActivity
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.MainActivity
import de.bitb.pantryplaner.core.misc.Logger
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.intro.SPLASH_TIMER
import de.bitb.pantryplaner.ui.intro.assertLoginPageRendered
import de.bitb.pantryplaner.ui.overview.assertOverviewPageRendered
import io.cucumber.java.en.Given
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@HiltAndroidTest
class StartingSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Given("Run App")
    fun runApp() {
        Logger.justPrint("runApp")
        SPLASH_TIMER = 0L
        scenarioData.launch()
        waitForIdle()
    }

    @Given("Start on LoginPage")
    fun startOnLoginPage() {
        Logger.justPrint("startOnLoginPage")
        runApp()
        assertLoginPageRendered()
    }

}