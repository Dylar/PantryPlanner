package de.bitb.pantryplaner.ui.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.NewAppVersionDialogTag
import io.cucumber.java.en.Then

@HiltAndroidTest
class NewAppVersionDialogSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("NewAppVersionDialog is displayed")
    fun newAppVersionDialogIsDisplayed() {
        onNodeWithTag(NewAppVersionDialogTag.DialogTag).assertIsDisplayed()
        onNodeWithTag(NewAppVersionDialogTag.ConfirmButton).assertIsDisplayed()
        onNodeWithTag(NewAppVersionDialogTag.CancelButton).assertIsDisplayed()
    }

    @Then("Tap on cancel NewAppVersionDialog")
    fun cancelNewAppVersionDialog() {
        onNodeWithTag(NewAppVersionDialogTag.CancelButton).performClick()
    }

}