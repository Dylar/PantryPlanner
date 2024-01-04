package de.bitb.pantryplaner.ui.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.SelectStockDialogTag
import io.cucumber.java.en.Then

@HiltAndroidTest
class SelectStockDialogSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("SelectStockDialog is displayed")
    fun selectStockDialogIsDisplayed() {
        onNodeWithTag(SelectStockDialogTag.DialogTag).assertIsDisplayed()
//        onNodeWithTag(SelectStockDialogTag.NewButton).assertIsDisplayed()
    }

    @Then("SelectStockDialog is NOT displayed")
    fun selectStockDialogIsNotDisplayed() {
        onNodeWithTag(SelectStockDialogTag.DialogTag).assertDoesNotExist()
//        onNodeWithTag(SelectStockDialogTag.NewButton).assertDoesNotExist()
    }

}