package de.bitb.pantryplaner.ui.comps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.AddEditStockDialogTag
import de.bitb.pantryplaner.ui.base.testTags.DropDownItemTag
import de.bitb.pantryplaner.ui.base.testTags.SearchDropDownTag
import de.bitb.pantryplaner.ui.base.testTags.SharedWithTag
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@HiltAndroidTest
class SearchDropDownSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("Open dropdown")
    fun openDropdown() {
        onNodeWithTag(SearchDropDownTag).performClick()
        waitForIdle()
    }

    @Then("Dropdown option {string} is visible")
    fun dropdownOptionIsVisible(name: String) {
        onNodeWithTag(DropDownItemTag(name)).assertIsDisplayed()
    }

    @Then("Select dropdown option {string}")
    fun selectDropdownOption(name: String) {
        onNodeWithTag(DropDownItemTag(name)).performClick()
        waitForIdle()
    }

}