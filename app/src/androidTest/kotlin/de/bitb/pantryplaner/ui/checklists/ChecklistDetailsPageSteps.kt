package de.bitb.pantryplaner.ui.checklists

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.assertNodeWithParentTagDoesNotExists
import de.bitb.pantryplaner.core.getParentTag
import de.bitb.pantryplaner.core.hasTextInHierarchy
import de.bitb.pantryplaner.core.onNodeWithParentTag
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.*
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

    @Then("ChecklistPage details is NOT rendered")
    fun detailsIsNotDisplayed() {
        assertNodeWithParentTagDoesNotExists(getParentTag("ChecklistPage"), SearchDropDownTag("Lager"))
        assertNodeWithParentTagDoesNotExists(
            getParentTag("ChecklistPage"),
            SearchDropDownTag("Mit Benutzer teilen")
        )
        onNodeWithTag(SharedWithTag.SharedWith).assertDoesNotExist()
    }

    @Then("ChecklistPage details is rendered")
    fun detailsIsDisplayed() {
        onNodeWithTag(SharedWithTag.SharedWith).assertIsDisplayed()
    }

    @Then("ChecklistPage tap on DetailsButton")
    fun tapOnDetailsButton() {
        onNodeWithTag(ChecklistPageTag.DetailsButton).performClick()
        waitForIdle()
    }

    @And("Checklist Stock {string} is displayed")
    fun checklistStockIsDisplayed(stock: String) {
        try {
            onNodeWithTag(SearchDropDownTag("Lager")).hasTextInHierarchy(stock)
        } catch (_: AssertionError) {
            tapOnDetailsButton()
            waitForIdle()
            onNodeWithTag(SearchDropDownTag("Lager")).hasTextInHierarchy(stock)
        }
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
    onNodeWithTag(ChecklistPageTag.DetailsButton).assertIsDisplayed()
    onNodeWithTag(FloatingExpandingButtonTag).assertIsDisplayed()
}
