package de.bitb.pantryplaner.ui.checklists

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.assertBottomNaviBar
import de.bitb.pantryplaner.ui.base.testTags.ChecklistsPageTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class ChecklistsPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("ChecklistsPage rendered")
    fun renderChecklistsPage() = assertChecklistsPageRendered()

    @When("Tap on NewChecklistButton")
    fun tapOnNewChecklistButton() {
        onNodeWithTag(ChecklistsPageTag.NewChecklistButton).performClick()
        waitForIdle()
    }

    @Then("NewChecklistButton is NOT displayed")
    fun newChecklistButtonIsNotDisplayed() {
        onNodeWithTag(ChecklistsPageTag.NewChecklistButton).assertDoesNotExist()
    }

    @Then("NewChecklistButton is displayed")
    fun newChecklistButtonIsDisplayed() {
        onNodeWithTag(ChecklistsPageTag.NewChecklistButton).assertIsDisplayed()
    }
}

fun ComposeTestRule.assertChecklistsPageRendered() {
    onNodeWithTag(ChecklistsPageTag.AppBar).assertIsDisplayed()
    onNodeWithTag(ChecklistsPageTag.LayoutButton).assertIsDisplayed()
    assertBottomNaviBar()
}