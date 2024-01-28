package de.bitb.pantryplaner.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.BottomNaviTag
import de.bitb.pantryplaner.ui.base.testTags.FloatingExpandingButtonTag
import de.bitb.pantryplaner.ui.base.testTags.TestTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class NavigationSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("BottomNaviBar rendered")
    fun renderBottomNaviBar() = assertBottomNaviBar()

    @Then("FloatingActionButton is NOT displayed")
    fun floatingExpandingButtonIsNotDisplayed() =
        onNodeWithTag(FloatingExpandingButtonTag).assertDoesNotExist()

    @When("Tap on FloatingActionButton")
    fun performTapOnFloatingActionButton() {
        onNodeWithTag(FloatingExpandingButtonTag).performClick()
    }

    @When("Navi to ChecklistsPage")
    fun performTapOnChecklistsButton() {
        tapOnChecklistsButton()
    }

    @When("Navi to RecipesPage")
    fun performTapOnRecipesButton() {
        tapOnRecipesButton()
    }

    @When("Navi to StocksPage")
    fun performTapOnStockButton() {
        tapOnStocksButton()
    }

    @When("Navi to ProfilePage")
    fun performTapOnProfileButton() {
        tapOnProfileButton()
    }

    @When("Navi to SettingsPage")
    fun performTapOnSettingsButton() {
        tapOnSettingsButton()
    }
}

fun ComposeTestRule.assertBottomNaviBar() {
    onNodeWithTag(BottomNaviTag.ChecklistsButton).assertIsDisplayed()
    onNodeWithTag(BottomNaviTag.StocksButton).assertIsDisplayed()
    onNodeWithTag(BottomNaviTag.RecipesButton).assertIsDisplayed()
    onNodeWithTag(BottomNaviTag.ProfileButton).assertIsDisplayed()
    onNodeWithTag(BottomNaviTag.SettingsButton).assertIsDisplayed()
}

fun ComposeTestRule.tapOnChecklistsButton() {
    onNodeWithTag(BottomNaviTag.ChecklistsButton).performClick()
    waitForIdle()
}

fun ComposeTestRule.tapOnRecipesButton() {
    onNodeWithTag(BottomNaviTag.RecipesButton).performClick()
    waitForIdle()
}

fun ComposeTestRule.tapOnStocksButton() {
    onNodeWithTag(BottomNaviTag.StocksButton).performClick()
    waitForIdle()
}

fun ComposeTestRule.tapOnProfileButton() {
    onNodeWithTag(BottomNaviTag.ProfileButton).performClick()
    waitForIdle()
}

fun ComposeTestRule.tapOnSettingsButton() {
    onNodeWithTag(BottomNaviTag.SettingsButton).performClick()
    waitForIdle()
}

fun ComposeTestRule.tapOnFloatingActionButton(tag: TestTag) {
    try {
        onNodeWithTag(tag).assertIsDisplayed()
    } catch (_: AssertionError) {
        onNodeWithTag(FloatingExpandingButtonTag).performClick()
        waitForIdle()
    }
    onNodeWithTag(tag).performClick()
    waitForIdle()
}