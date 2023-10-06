package de.bitb.pantryplaner.ui.checklist

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.ChecklistPageTag
import de.bitb.pantryplaner.ui.base.testTags.SearchDropDownTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class ChecklistPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("ChecklistPage rendered")
    fun renderChecklistPage() {
        assertChecklistPageRendered()
    }

    @When("Tap on AddItemButton")
    fun tapOnAddItemButton() {
        onNodeWithTag(ChecklistPageTag.AddItemButton).performClick()
        waitForIdle()
    }

    @When("Tap on FinishButton")
    fun tapOnFinishButton() {
        onNodeWithTag(ChecklistPageTag.FinishButton).performClick()
        waitForIdle()
    }
}

fun ComposeTestRule.assertChecklistPageRendered() {
    onNodeWithTag(ChecklistPageTag.ChecklistPage).assertIsDisplayed()
    onNodeWithTag(ChecklistPageTag.AppBar).assertIsDisplayed()
    onNodeWithTag(ChecklistPageTag.LayoutButton).assertIsDisplayed()
    onNodeWithTag(ChecklistPageTag.FilterButton).assertIsDisplayed()
    onNodeWithTag(ChecklistPageTag.AddItemButton).assertIsDisplayed()
    onNodeWithTag(ChecklistPageTag.FinishButton).assertIsDisplayed()
}

