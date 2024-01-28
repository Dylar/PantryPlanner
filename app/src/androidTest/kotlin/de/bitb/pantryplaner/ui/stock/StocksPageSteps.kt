package de.bitb.pantryplaner.ui.stock

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.*
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.*
import de.bitb.pantryplaner.ui.tapOnFloatingActionButton
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class StocksPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("StocksPage rendered")
    fun renderStocksPage() {
        assertStocksPageRendered()
    }

    @Then("StocksPage {string} details is NOT rendered")
    fun detailsIsNotDisplayed(stock: String) {
        onNodeWithTag(SharedWithTag.SharedWith).assertDoesNotExist()
    }

    @Then("StocksPage {string} details is rendered")
    fun detailsIsDisplayed(stock: String) {
        onNodeWithTag(SharedWithTag.SharedWith).assertIsDisplayed()
    }

    @Then("StocksPage tap on DetailsButton")
    fun tapOnDetailsButton() {
        onNodeWithTag(StocksPageTag.DetailsButton).performClick()
        waitForIdle()
    }

    @When("Tab {string} is displayed")
    fun tabIsDisplayed(name: String) {
        onNodeWithTag(StocksPageTag.StockTabTag(name)).assertIsDisplayed()
    }

    @When("Tab {string} is NOT displayed")
    fun tabIsNotDisplayed(name: String) {
        onNodeWithTag(StocksPageTag.StockTabTag(name)).assertDoesNotExist()
    }

    @When("Tap on tab {string}")
    fun tapOnTab(name: String) {
        onNodeWithTag(StocksPageTag.StockTabTag(name)).performClick()
        waitForIdle()
    }

    @When("Tap on StocksPage NewStockButton")
    fun tapOnNewStockButton() {
        tapOnFloatingActionButton(StocksPageTag.NewStockButton)
    }

    @When("Tap on StocksPage NewItemButton")
    fun tapOnNewItemButton() {
        tapOnFloatingActionButton(StocksPageTag.NewItemButton)
    }

    @Then("NewItemButton is NOT displayed")
    fun newItemButtonIsNotDisplayed() {
        onNodeWithTag(StocksPageTag.NewItemButton).assertDoesNotExist()
    }

    @Then("NewItemButton is displayed")
    fun newItemButtonIsDisplayed() {
        onNodeWithTag(StocksPageTag.NewItemButton).assertIsDisplayed()
    }
}

fun ComposeTestRule.assertStocksPageRendered() {
    onNodeWithTag(StocksPageTag.AppBar).assertIsDisplayed()
    onNodeWithTag(StocksPageTag.SearchButton).assertIsDisplayed()
    onNodeWithTag(StocksPageTag.LayoutButton).assertIsDisplayed()
    onNodeWithTag(StocksPageTag.DetailsButton).assertIsDisplayed()
    onNodeWithTag(FloatingExpandingButtonTag).assertIsDisplayed()
}

