package de.bitb.pantryplaner.ui.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.hasTextInHierarchy
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.AddEditChecklistDialogTag
import de.bitb.pantryplaner.ui.base.testTags.SearchDropDownTag
import de.bitb.pantryplaner.ui.base.testTags.SelectChecklistDialogTag
import io.cucumber.java.en.And
import io.cucumber.java.en.Then

@HiltAndroidTest
class SelectChecklistDialogSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("SelectChecklistDialog is displayed")
    fun selectChecklistDialogIsDisplayed() {
        onNodeWithTag(SelectChecklistDialogTag.DialogTag).assertIsDisplayed()
//        onNodeWithTag(SelectChecklistDialogTag.NewButton).assertIsDisplayed()
    }

    @Then("SelectChecklistDialog is NOT displayed")
    fun selectChecklistDialogIsNotDisplayed() {
        onNodeWithTag(SelectChecklistDialogTag.DialogTag).assertDoesNotExist()
//        onNodeWithTag(SelectChecklistDialogTag.NewButton).assertDoesNotExist()
    }

}