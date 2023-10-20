package de.bitb.pantryplaner.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.getString
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.StockTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class StockSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("No Stocks displayed")
    fun noStocksDisplayed() {
        onNodeWithText(getString(R.string.no_stocks)).assertIsDisplayed()
        waitForIdle()
    }

    @Then("Stock {string} is displayed")
    fun stockIsDisplayed(name: String) {
        onNodeWithTag(StockTag(name), true).assertIsDisplayed()
        waitForIdle()
    }

    @Then("Stock {string} is NOT displayed")
    fun stockIsNotDisplayed(name: String) {
        onNodeWithTag(StockTag(name), true).assertDoesNotExist()
        waitForIdle()
    }

    @Then("Swipe to remove Stock {string}")
    fun swipeToRemoveStock(name: String) {
        //TODO do we need unmergedTree?
        onNodeWithTag(StockTag(name), true)
            .performTouchInput { swipeRight() }
        waitForIdle()
    }

    @When("LongPress on Stock {string}")
    fun longPressOnStock(name: String) {
        onNodeWithTag(StockTag(name), true)
            .performTouchInput { longClick() }
        waitForIdle()
    }
}