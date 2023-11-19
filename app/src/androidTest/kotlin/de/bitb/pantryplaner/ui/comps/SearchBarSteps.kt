package de.bitb.pantryplaner.ui.comps

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.espresso.Espresso
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.SearchBarTag
import de.bitb.pantryplaner.ui.base.testTags.SelectItemsPageTag
import de.bitb.pantryplaner.ui.base.testTags.StocksPageTag
import io.cucumber.java.en.Then

@HiltAndroidTest
class SearchBarSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("Tap SearchBar on SelectItemPage")
    fun tapSearchBarOnSelectItemPage() {
        onNodeWithTag(SelectItemsPageTag.SearchButton).performClick()
        waitForIdle()
    }

    @Then("Tap SearchBar on StocksPage")
    fun tapSearchBarOnStocksPage() {
        onNodeWithTag(StocksPageTag.SearchButton).performClick()
        waitForIdle()
    }

    @Then("Input search {string}")
    fun inputSearch(search: String) {
        onNodeWithTag(SearchBarTag).performTextReplacement(search)
        Espresso.closeSoftKeyboard()
        waitForIdle()
    }

}