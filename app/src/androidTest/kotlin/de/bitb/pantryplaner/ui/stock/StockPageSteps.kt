package de.bitb.pantryplaner.ui.stock

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.ProfilePageTag
import de.bitb.pantryplaner.ui.base.testTags.StockPageTag
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

    @When("Tap NewItemButton")
    fun tapNewStockButton() {
        onNodeWithTag(StockPageTag.NewItemButton).performClick()
        waitForIdle()
    }
}

fun ComposeTestRule.assertStockPageRendered() {
    onNodeWithTag(StockPageTag.AppBar).assertIsDisplayed()
    onNodeWithTag(StockPageTag.SearchButton).assertIsDisplayed()
    onNodeWithTag(StockPageTag.LayoutButton).assertIsDisplayed()
    onNodeWithTag(StockPageTag.FilterButton).assertIsDisplayed()
    onNodeWithTag(StockPageTag.NewItemButton).assertIsDisplayed()
}

