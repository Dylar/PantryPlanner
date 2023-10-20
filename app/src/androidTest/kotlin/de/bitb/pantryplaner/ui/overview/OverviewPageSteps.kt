package de.bitb.pantryplaner.ui.overview

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.OverviewPageTag
import de.bitb.pantryplaner.ui.base.testTags.StockPageTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class OverviewPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("OverviewPage rendered")
    fun renderOverviewPage() = assertOverviewPageRendered()

    @When("Tap on ProfileButton")
    fun tapOnProfileButtonStep() {
        tapOnProfileButton()
    }

    @When("Tap on StockButton")
    fun performTapOnStockButton() {
        tapOnStockButton()
    }

    @When("Tap on NewChecklistButton")
    fun tapOnNewChecklistButton() {
        onNodeWithTag(OverviewPageTag.NewChecklistButton).performClick()
        waitForIdle()
    }

    @Then("NewChecklistButton is NOT displayed")
    fun newChecklistButtonIsNotDisplayed() {
        onNodeWithTag(OverviewPageTag.NewChecklistButton).assertDoesNotExist()
    }

    @Then("NewChecklistButton is displayed")
    fun newChecklistButtonIsDisplayed() {
        onNodeWithTag(OverviewPageTag.NewChecklistButton).assertIsDisplayed()
    }
}

fun ComposeTestRule.assertOverviewPageRendered() {
    onNodeWithTag(OverviewPageTag.AppBar).assertIsDisplayed()
    onNodeWithTag(OverviewPageTag.ProfileButton).assertIsDisplayed()
    onNodeWithTag(OverviewPageTag.LayoutButton).assertIsDisplayed()
    onNodeWithTag(OverviewPageTag.StockButton).assertIsDisplayed()
}

fun ComposeTestRule.tapOnProfileButton() {
    onNodeWithTag(OverviewPageTag.ProfileButton).performClick()
    waitForIdle()
}

fun ComposeTestRule.tapOnStockButton() {
    onNodeWithTag(OverviewPageTag.StockButton).performClick()
    waitForIdle()
}