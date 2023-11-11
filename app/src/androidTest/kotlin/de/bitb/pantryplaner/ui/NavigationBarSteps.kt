package de.bitb.pantryplaner.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.BottomNaviTag
import de.bitb.pantryplaner.ui.base.testTags.ExpandingFloatingButtonTag
import de.bitb.pantryplaner.ui.base.testTags.StockPageTag
import de.bitb.pantryplaner.ui.base.testTags.TestTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class OverviewPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("BottomNaviBar rendered")
    fun renderBottomNaviBar() = assertBottomNaviBar()

    @When("Tap on OverviewButton")
    fun performTapOnOverviewButton() {
        tapOnOverviewButton()
    }

    @When("Tap on StockButton")
    fun performTapOnStockButton() {
        tapOnStockButton()
    }

    @When("Tap on ProfileButton")
    fun performTapOnProfileButton() {
        tapOnProfileButton()
    }

    @When("Tap on SettingsButton")
    fun performTapOnSettingsButton() {
        tapOnSettingsButton()
    }

}

fun ComposeTestRule.assertBottomNaviBar() {
    onNodeWithTag(BottomNaviTag.OverviewButton).assertIsDisplayed()
    onNodeWithTag(BottomNaviTag.StockButton).assertIsDisplayed()
    onNodeWithTag(BottomNaviTag.ProfileButton).assertIsDisplayed()
    onNodeWithTag(BottomNaviTag.SettingsButton).assertIsDisplayed()
}

fun ComposeTestRule.tapOnOverviewButton() {
    onNodeWithTag(BottomNaviTag.OverviewButton).performClick()
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
        onNodeWithTag(ExpandingFloatingButtonTag).performClick()
        waitForIdle()
    }
    onNodeWithTag(tag).performClick()
    waitForIdle()
}