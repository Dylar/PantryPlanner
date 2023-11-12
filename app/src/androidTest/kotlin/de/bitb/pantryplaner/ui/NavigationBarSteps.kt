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
class OverviewPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("BottomNaviBar rendered")
    fun renderBottomNaviBar() = assertBottomNaviBar()

    @When("Navi to OverviewButton")
    fun performTapOnOverviewButton() {
        tapOnOverviewButton()
    }

    @When("Navi to RecipePage")
    fun performTapOnRecipeButton() {
        tapOnRecipeButton()
    }

    @When("Navi to StockPage")
    fun performTapOnStockButton() {
        tapOnStockButton()
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
    onNodeWithTag(BottomNaviTag.OverviewButton).assertIsDisplayed()
    onNodeWithTag(BottomNaviTag.StockButton).assertIsDisplayed()
    onNodeWithTag(BottomNaviTag.RecipeButton).assertIsDisplayed()
    onNodeWithTag(BottomNaviTag.ProfileButton).assertIsDisplayed()
    onNodeWithTag(BottomNaviTag.SettingsButton).assertIsDisplayed()
}

fun ComposeTestRule.tapOnOverviewButton() {
    onNodeWithTag(BottomNaviTag.OverviewButton).performClick()
    waitForIdle()
}

fun ComposeTestRule.tapOnRecipeButton() {
    onNodeWithTag(BottomNaviTag.RecipeButton).performClick()
    waitForIdle()
}

fun ComposeTestRule.tapOnStockButton() {
    onNodeWithTag(BottomNaviTag.StockButton).performClick()
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
    } catch (e: AssertionError) {
        onNodeWithTag(FloatingExpandingButtonTag).performClick()
        waitForIdle()
    }
    onNodeWithTag(tag).performClick()
    waitForIdle()
}