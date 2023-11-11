package de.bitb.pantryplaner.ui.stock

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.ExpandingFloatingButtonTag
import de.bitb.pantryplaner.ui.base.testTags.StockPageTag
import de.bitb.pantryplaner.ui.tapOnFloatingActionButton
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class StockPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("StockPage rendered")
    fun renderStockPage() {
        assertStockPageRendered()
    }

    @When("Tab {string} is displayed")
    fun tabIsDisplayed(name: String) {
        onNodeWithTag(StockPageTag.StockTabTag(name)).assertIsDisplayed()
    }

    @When("Tab {string} is NOT displayed")
    fun tabIsNotDisplayed(name: String) {
        onNodeWithTag(StockPageTag.StockTabTag(name)).assertDoesNotExist()
    }

    @When("Tap on tab {string}")
    fun tapOnTab(name: String) {
        onNodeWithTag(StockPageTag.StockTabTag(name)).performClick()
        waitForIdle()
    }

    @When("Tap on StockPage NewStockButton")
    fun tapOnNewStockButton() {
        tapOnFloatingActionButton(StockPageTag.NewStockButton)
    }

    @When("Tap on NewItemButton")
    fun tapOnNewItemButton() {
        tapOnFloatingActionButton(StockPageTag.NewItemButton)
    }

    @Then("NewItemButton is NOT displayed")
    fun newItemButtonIsNotDisplayed() {
        onNodeWithTag(StockPageTag.NewItemButton).assertDoesNotExist()
    }

    @Then("NewItemButton is displayed")
    fun newItemButtonIsDisplayed() {
        onNodeWithTag(StockPageTag.NewItemButton).assertIsDisplayed()
    }
}

fun ComposeTestRule.assertStockPageRendered() {
    onNodeWithTag(StockPageTag.AppBar).assertIsDisplayed()
    onNodeWithTag(StockPageTag.SearchButton).assertIsDisplayed()
    onNodeWithTag(StockPageTag.LayoutButton).assertIsDisplayed()
    onNodeWithTag(StockPageTag.FilterButton).assertIsDisplayed()
    onNodeWithTag(ExpandingFloatingButtonTag).assertIsDisplayed()
}

