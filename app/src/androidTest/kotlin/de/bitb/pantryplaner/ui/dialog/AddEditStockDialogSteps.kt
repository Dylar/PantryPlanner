package de.bitb.pantryplaner.ui.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.AddEditStockDialogTag
import io.cucumber.java.en.And
import io.cucumber.java.en.Then

@HiltAndroidTest
class AddEditStockDialogSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("AddEditStockDialog is displayed")
    fun addEditStockDialogIsDisplayed() {
        assertAddEditStockDialogRendered()
    }

    @And("Input {string} as Stock name")
    fun inputAsStockName(input: String) {
        onNodeWithTag(AddEditStockDialogTag.NameLabel).performTextClearance()
        onNodeWithTag(AddEditStockDialogTag.NameLabel).performTextInput(input)
        waitForIdle()
    }

    @And("Tap on CreateStockButton")
    fun tapOnCreateStockButton() {
        onNodeWithTag(AddEditStockDialogTag.ConfirmButton).performClick()
        waitForIdle()
    }

}

fun ComposeTestRule.assertAddEditStockDialogRendered() {
    onNodeWithTag(AddEditStockDialogTag.NameLabel).assertIsDisplayed()
    onNodeWithTag(AddEditStockDialogTag.ConfirmButton).assertIsDisplayed()
}