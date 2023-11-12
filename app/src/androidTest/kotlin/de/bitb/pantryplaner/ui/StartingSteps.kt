package de.bitb.pantryplaner.ui

import androidx.compose.ui.test.junit4.ComposeTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.test.defaultPW
import de.bitb.pantryplaner.test.parseUser
import de.bitb.pantryplaner.ui.checklists.assertChecklistsPageRendered
import de.bitb.pantryplaner.ui.checklists.assertChecklistPageRendered
import de.bitb.pantryplaner.ui.intro.SPLASH_TIMER
import de.bitb.pantryplaner.ui.intro.assertLoginPageRendered
import de.bitb.pantryplaner.ui.intro.loginUserWith
import de.bitb.pantryplaner.ui.checklists.tapOnChecklist
import de.bitb.pantryplaner.ui.profile.assertProfilePageRendered
import de.bitb.pantryplaner.ui.recipe.assertRecipesPageRendered
import de.bitb.pantryplaner.ui.settings.assertSettingsPageRendered
import de.bitb.pantryplaner.ui.stock.INSTANT_SEARCH
import de.bitb.pantryplaner.ui.stock.assertStocksPageRendered
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

    @Given("Start on ChecklistsPage")
    fun startOnChecklistsPage() {
        startOnLoginPage()
        val user = parseUser()
        loginUserWith(user.email, defaultPW)
        assertChecklistsPageRendered()
    }

    @Given("Start on ChecklistPage {string}")
    fun startOnChecklistPage(name: String) {
        startOnChecklistsPage()
        tapOnChecklist(name)
        assertChecklistPageRendered()
    }

    @Given("Start on RecipesPage")
    fun startOnRecipesPage() {
        startOnChecklistsPage()
        tapOnRecipesButton()
        assertRecipesPageRendered()
    }

    @Given("Start on StocksPage")
    fun startOnStocksPage() {
        startOnChecklistsPage()
        tapOnStocksButton()
        assertStocksPageRendered()
    }

    @Given("Start on ProfilePage")
    fun startOnProfilePage() {
        startOnChecklistsPage()
        tapOnProfileButton()
        assertProfilePageRendered()
    }

    @Given("Start on SettingsPage")
    fun startOnSettingsPage() {
        startOnChecklistsPage()
        tapOnSettingsButton()
        assertSettingsPageRendered()
    }

}