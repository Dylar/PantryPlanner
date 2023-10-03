package de.bitb.pantryplaner.ui.overview

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
import de.bitb.pantryplaner.ui.base.testTags.ChecklistTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class ChecklistSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("No Checklists displayed")
    fun noChecklistsDisplayed() {
        onNodeWithText(getString(R.string.no_checklists)).assertIsDisplayed()
        waitForIdle()
    }

    @Then("Checklist {string} is displayed")
    fun checklistIsDisplayed(name: String) {
        onNodeWithTag(ChecklistTag( name), true).assertIsDisplayed()
        waitForIdle()
    }

    @Then("Checklist {string} is NOT displayed")
    fun checklistIsNotDisplayed(name: String) {
        onNodeWithTag(ChecklistTag( name), true).assertDoesNotExist()
        waitForIdle()
    }

    @Then("Swipe to remove Checklist {string}")
    fun swipeToRemoveChecklist(name: String) {
        onNodeWithTag(ChecklistTag( name), true).performTouchInput { swipeRight() }
        waitForIdle()
    }

    @When("LongPress on Checklist {string}")
    fun longPressOnChecklist(name: String) {
        onNodeWithTag(ChecklistTag( name), true).performTouchInput { longClick() }
        waitForIdle()
    }
}