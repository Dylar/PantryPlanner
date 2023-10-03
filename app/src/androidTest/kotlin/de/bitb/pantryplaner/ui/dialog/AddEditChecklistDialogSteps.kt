package de.bitb.pantryplaner.ui.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.hasTextInHierarchy
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.AddEditChecklistDialogTag
import de.bitb.pantryplaner.ui.base.testTags.AddEditItemDialogTag
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@HiltAndroidTest
class AddEditChecklistDialogSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("AddEditChecklistDialog is displayed")
    fun addEditChecklistDialogIsDisplayed() {
        assertAddEditChecklistDialogRendered()
    }

    @Then("Checklist name is {string}")
    fun itemNameIs(name: String) {
        onNodeWithTag(AddEditChecklistDialogTag.NameLabel)
            .hasTextInHierarchy(name)
            .assertIsDisplayed()
    }

    @And("Input {string} as Checklist name")
    fun inputAsChecklistName(input: String) {
        onNodeWithTag(AddEditChecklistDialogTag.NameLabel).performTextClearance()
        onNodeWithTag(AddEditChecklistDialogTag.NameLabel).performTextInput(input)
        waitForIdle()
    }

    @And("Tap on CreateChecklistButton")
    fun tapOnCreateChecklistButton() {
        onNodeWithTag(AddEditChecklistDialogTag.ConfirmButton).performClick()
        waitForIdle()
    }

}

fun ComposeTestRule.assertAddEditChecklistDialogRendered() {
    onNodeWithTag(AddEditChecklistDialogTag.NameLabel).assertIsDisplayed()
    onNodeWithTag(AddEditChecklistDialogTag.ConfirmButton).assertIsDisplayed()
}