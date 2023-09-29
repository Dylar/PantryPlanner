package de.bitb.pantryplaner.ui.comps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.DropDownItemTag
import de.bitb.pantryplaner.ui.base.testTags.SearchDropDownTag
import io.cucumber.java.en.Then

@HiltAndroidTest
class SearchDropDownSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("Open dropdown {string}")
    fun openDropdown(hint: String) {
        onNodeWithTag(SearchDropDownTag(hint)).performClick()
        waitForIdle()
    }

    @Then("Dropdown option {string} is displayed")
    fun dropdownOptionIsDisplayed(name: String) {
        onNodeWithTag(DropDownItemTag(name)).assertIsDisplayed()
    }

    @Then("Select dropdown option {string}")
    fun selectDropdownOption(name: String) {
        onNodeWithTag(DropDownItemTag(name)).performClick()
        waitForIdle()
    }

}