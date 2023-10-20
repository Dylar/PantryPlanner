package de.bitb.pantryplaner.ui.comps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.assertNodeWithParentTagDoesNotExists
import de.bitb.pantryplaner.core.getParentTag
import de.bitb.pantryplaner.core.onNodeWithParentTag
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.core.waitFor
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.DropDownItemTag
import de.bitb.pantryplaner.ui.base.testTags.SearchDropDownTag
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then

@HiltAndroidTest
class SearchDropDownSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Given("{string} dropdown {string} is NOT displayed")
    fun dropdownIsNotDisplayed(parent: String, hint: String) {
        assertNodeWithParentTagDoesNotExists(getParentTag(parent), SearchDropDownTag(hint))
    }

//    @Given("{string} dropdown {string} is NOT displayed")
//    fun dropdownIsNotDisplayed(parent: String, hint: String) {
//        asserWith(getParentTag(parent), SearchDropDownTag(hint))
//    }

    @Then("{string} open dropdown {string}")
    fun openDropdown(parent: String, hint: String) {
        onNodeWithParentTag(getParentTag(parent), SearchDropDownTag(hint)).performClick()
        waitForIdle()
    }

    @And("Input {string} as Item category")
    fun inputAsItemCategory(input: String) {
        onNodeWithTag(SearchDropDownTag("Kategorie")).performTextReplacement(input)
        waitForIdle()
    }

    @And("Input {string} as Checklist Stock")
    fun inputAsChecklistStock(input: String) {
        onNodeWithTag(SearchDropDownTag("Lager")).performTextReplacement(input)
        waitForIdle()
    }

    @Then("Dropdown option {string} is displayed")
    fun dropdownOptionIsDisplayed(name: String) {
        onNodeWithTag(DropDownItemTag(name)).assertIsDisplayed()
    }

    @Then("Dropdown option {string} is NOT displayed")
    fun dropdownOptionIsNotDisplayed(name: String) {
        onNodeWithTag(DropDownItemTag(name)).assertDoesNotExist()
    }

    @Then("Select dropdown option {string}")
    fun selectDropdownOption(name: String) {
        onNodeWithTag(DropDownItemTag(name)).performClick()
        waitForIdle()
    }

}