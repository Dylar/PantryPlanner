package de.bitb.pantryplaner.ui.comps

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.core.sleepFor
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.SearchBarTag
import de.bitb.pantryplaner.ui.base.testTags.StockPageTag
import io.cucumber.java.en.Then

@HiltAndroidTest
class SearchBarSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("Tap on SearchBar")
    fun tapOnSearchBar() {
        onNodeWithTag(StockPageTag.SearchButton).performClick()
        waitForIdle()
    }

    @Then("Input search {string}")
    fun inputSearch(search: String) {
        onNodeWithTag(SearchBarTag).performTextClearance()
        onNodeWithTag(SearchBarTag).performTextInput(search)
        Espresso.closeSoftKeyboard()
        waitForIdle()
        sleepFor()
    }

}