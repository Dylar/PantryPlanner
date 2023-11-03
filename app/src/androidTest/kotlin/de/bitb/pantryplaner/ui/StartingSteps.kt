package de.bitb.pantryplaner.ui

import androidx.compose.ui.test.junit4.ComposeTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.test.defaultPW
import de.bitb.pantryplaner.test.parseUser
import de.bitb.pantryplaner.ui.base.SNACKBARS_ENABLED
import de.bitb.pantryplaner.ui.checklist.assertChecklistPageRendered
import de.bitb.pantryplaner.ui.intro.SPLASH_TIMER
import de.bitb.pantryplaner.ui.intro.assertLoginPageRendered
import de.bitb.pantryplaner.ui.intro.loginUserWith
import de.bitb.pantryplaner.ui.overview.assertOverviewPageRendered
import de.bitb.pantryplaner.ui.overview.tapOnChecklist
import de.bitb.pantryplaner.ui.overview.tapOnProfileButton
import de.bitb.pantryplaner.ui.overview.tapOnStockButton
import de.bitb.pantryplaner.ui.profile.assertProfilePageRendered
import de.bitb.pantryplaner.ui.profile.tapOnSettingsButton
import de.bitb.pantryplaner.ui.settings.assertSettingsPageRendered
import de.bitb.pantryplaner.ui.stock.INSTANT_SEARCH
import de.bitb.pantryplaner.ui.stock.assertStockPageRendered
import io.cucumber.java.en.Given

@HiltAndroidTest
class StartingSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Given("Run App")
    fun runApp() {
        SPLASH_TIMER = 0L
        INSTANT_SEARCH = true
        scenarioData.launch()
        waitForIdle()
    }

    @Given("Start on LoginPage")
    fun startOnLoginPage() {
        runApp()
        assertLoginPageRendered()
    }

    @Given("Start on OverviewPage")
    fun startOnOverviewPage() {
        startOnLoginPage()
        val user = parseUser()
        loginUserWith(user.email, defaultPW)
        assertOverviewPageRendered()
    }

    @Given("Start on ChecklistPage {string}")
    fun startOnChecklistPage(name: String) {
        startOnOverviewPage()
        tapOnChecklist(name)
        assertChecklistPageRendered()
    }

    @Given("Start on StockPage")
    fun startOnStockPage() {
        startOnOverviewPage()
        tapOnStockButton()
        assertStockPageRendered()
    }

    @Given("Start on ProfilePage")
    fun startOnProfilePage() {
        startOnOverviewPage()
        tapOnProfileButton()
        assertProfilePageRendered()
    }

    @Given("Start on SettingsPage")
    fun startOnSettingsPage() {
        startOnProfilePage()
        tapOnSettingsButton()
        assertSettingsPageRendered()
    }

}