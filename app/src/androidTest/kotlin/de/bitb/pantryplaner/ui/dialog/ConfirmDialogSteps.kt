package de.bitb.pantryplaner.ui.dialog

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.core.sleepFor
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.ConfirmDialogTag
import io.cucumber.java.en.And

@HiltAndroidTest
class ConfirmDialogSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @And("Tap on Confirm")
    fun tapOnConfirm() {
        onNodeWithTag(ConfirmDialogTag.ConfirmButton).performClick()
        waitForIdle()
        sleepFor()
    }

    @And("Tap on dismiss")
    fun tapOnDismiss() {
        onNodeWithTag(ConfirmDialogTag.DismissButton).performClick()
        waitForIdle()
    }
}
