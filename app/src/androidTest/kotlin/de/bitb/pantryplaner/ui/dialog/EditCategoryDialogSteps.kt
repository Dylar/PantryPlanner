package de.bitb.pantryplaner.ui.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.EditCategoryDialogTag
import io.cucumber.java.en.And
import io.cucumber.java.en.Then

@HiltAndroidTest
class EditCategoryDialogSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("EditCategoryDialog is displayed")
    fun editCategoryDialogIsDisplayed() {
        onNodeWithTag(EditCategoryDialogTag.DialogTag).assertIsDisplayed()
        onNodeWithTag(EditCategoryDialogTag.SaveButton).assertIsDisplayed()
    }

    @And("Input {string} as category")
    fun inputAsItemCategory(input: String) {
        onNodeWithTag(EditCategoryDialogTag.CategoryLabel).performTextReplacement(input)
        waitForIdle()
    }

    @And("Tap on SaveCategoryButton")
    fun tapOnSave() {
        onNodeWithTag(EditCategoryDialogTag.SaveButton).performClick()
        waitForIdle()
    }

}