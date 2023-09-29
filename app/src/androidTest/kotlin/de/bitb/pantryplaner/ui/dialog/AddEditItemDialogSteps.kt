package de.bitb.pantryplaner.ui.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.AddEditItemDialogTag
import io.cucumber.java.en.And
import io.cucumber.java.en.Then

@HiltAndroidTest
class AddEditItemDialogSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("AddEditItemDialog is displayed")
    fun addEditItemDialogIsDisplayed() {
        assertAddEditItemDialogRendered()
    }

    @And("Input {string} as Item name")
    fun inputAsItemName(input: String) {
        onNodeWithTag(AddEditItemDialogTag.NameLabel).performTextClearance()
        onNodeWithTag(AddEditItemDialogTag.NameLabel).performTextInput(input)
        waitForIdle()
    }

    @And("Tap on CreateItemButton")
    fun tapOnCreateItemButton() {
        onNodeWithTag(AddEditItemDialogTag.ConfirmButton).performClick()
        waitForIdle()
    }

}

fun ComposeTestRule.assertAddEditItemDialogRendered() {
    onNodeWithTag(AddEditItemDialogTag.NameLabel).assertIsDisplayed()
    onNodeWithTag(AddEditItemDialogTag.ConfirmButton).assertIsDisplayed()
}