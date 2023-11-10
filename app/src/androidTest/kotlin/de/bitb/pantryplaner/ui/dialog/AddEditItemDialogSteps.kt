package de.bitb.pantryplaner.ui.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.hasTextInHierarchy
import de.bitb.pantryplaner.core.onNodeWithParentTag
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.AddEditItemDialogTag
import de.bitb.pantryplaner.ui.base.testTags.SearchDropDownTag
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

    @Then("Item name is {string}")
    fun itemNameIs(name: String) {
        onNodeWithParentTag(
            AddEditItemDialogTag.DialogTag,
            AddEditItemDialogTag.NameLabel
        ).hasTextInHierarchy(name)
            .assertIsDisplayed()
    }

    @Then("Item category is {string}")
    fun itemCategoryIs(name: String) {
        onNodeWithTag(SearchDropDownTag("Kategorie"))
            .hasTextInHierarchy(name)
            .assertIsDisplayed()
    }

    @And("Input {string} as Item name")
    fun inputAsItemName(input: String) {
        onNodeWithParentTag(
            AddEditItemDialogTag.DialogTag,
            AddEditItemDialogTag.NameLabel
        ).performTextReplacement(input)
        waitForIdle()
    }

    @And("Tap on CreateItemButton")
    fun tapOnCreateItemButton() {
        onNodeWithTag(AddEditItemDialogTag.ConfirmButton).performClick()
        waitForIdle()
    }

    @And("Tap on cancel ItemDialog")
    fun tapOnCancelItemDialog() {
        onNodeWithTag(AddEditItemDialogTag.CancelButton).performClick()
        waitForIdle()
    }

}

fun ComposeTestRule.assertAddEditItemDialogRendered() {
    onNodeWithParentTag(
        AddEditItemDialogTag.DialogTag,
        AddEditItemDialogTag.NameLabel
    ).assertIsDisplayed()
    onNodeWithTag(AddEditItemDialogTag.ConfirmButton).assertIsDisplayed()
}