package de.bitb.pantryplaner.ui.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.AddUserDialogTag
import io.cucumber.java.en.And
import io.cucumber.java.en.Then

@HiltAndroidTest
class AddUserDialogSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("AddUserDialog is displayed")
    fun addUserDialogIsDisplayed() {
        onNodeWithTag(AddUserDialogTag.EmailLabel).assertIsDisplayed()
        onNodeWithTag(AddUserDialogTag.ConfirmButton).assertIsDisplayed()
    }

    @And("Input {string} as User email")
    fun inputAsStockName(input: String) {
        onNodeWithTag(AddUserDialogTag.EmailLabel).performTextReplacement(input)
        waitForIdle()
    }

    @And("Tap on ConnectUserButton")
    fun tapOnConnectUserButton() {
        onNodeWithTag(AddUserDialogTag.ConfirmButton).performClick()
        waitForIdle()
    }

    @And("Tap on ScanButton")
    fun tapOnScanButton() {
        onNodeWithTag(AddUserDialogTag.ScanButton).performClick()
        waitForIdle()
    }

}