package de.bitb.pantryplaner.ui.checklists

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.hasTextInHierarchy
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.ChecklistPageTag
import de.bitb.pantryplaner.ui.base.testTags.FloatingExpandingButtonTag
import de.bitb.pantryplaner.ui.base.testTags.SearchDropDownTag
import de.bitb.pantryplaner.ui.tapOnFloatingActionButton
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class ChecklistDetailsPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("ChecklistPage rendered")
    fun renderChecklistPage() {
        assertChecklistPageRendered()
    }

    @And("Checklist Stock {string} is displayed")
    fun inputAsChecklistStock(stock: String) {
        onNodeWithTag(SearchDropDownTag("Lager")).hasTextInHierarchy(stock)
        waitForIdle()
    }

    @When("Tap on ChecklistPage AddItemButton")
    fun tapOnAddItemButton() {
        tapOnFloatingActionButton(ChecklistPageTag.AddItemButton)
    }

    @When("Tap on FinishButton")
    fun tapOnFinishButton() {
        tapOnFloatingActionButton(ChecklistPageTag.FinishButton)
    }
}

fun ComposeTestRule.assertChecklistPageRendered() {
    onNodeWithTag(ChecklistPageTag.ChecklistPage).assertIsDisplayed()
    onNodeWithTag(ChecklistPageTag.AppBar).assertIsDisplayed()
    onNodeWithTag(ChecklistPageTag.LayoutButton).assertIsDisplayed()
    onNodeWithTag(ChecklistPageTag.FilterButton).assertIsDisplayed()
    onNodeWithTag(FloatingExpandingButtonTag).assertIsDisplayed()
}
