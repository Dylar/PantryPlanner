package de.bitb.pantryplaner.ui.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.AddEditLocationDialogTag
import io.cucumber.java.en.And
import io.cucumber.java.en.Then

@HiltAndroidTest
class NewLocationDialogSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("AddEditLocationDialog is displayed")
    fun addEditLocationDialogIsDisplayed() {
        assertAddEditLocationDialogRendered()
    }

    @And("Input {string} as Location name")
    fun inputAsLocationName(input: String) {
        onNodeWithTag(AddEditLocationDialogTag.NameLabel).performTextClearance()
        onNodeWithTag(AddEditLocationDialogTag.NameLabel).performTextInput(input)
        waitForIdle()
    }

    @And("Tap on CreateLocationButton")
    fun tapOnCreateLocationButton() {
        onNodeWithTag(AddEditLocationDialogTag.ConfirmButton).performClick()
        waitForIdle()
    }

}

fun ComposeTestRule.assertAddEditLocationDialogRendered() {
    onNodeWithTag(AddEditLocationDialogTag.NameLabel).assertIsDisplayed()
    onNodeWithTag(AddEditLocationDialogTag.ConfirmButton).assertIsDisplayed()
}