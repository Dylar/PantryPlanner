package de.bitb.pantryplaner.ui.SelectItems

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.SelectItemsPageTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@HiltAndroidTest
class SelectItemsPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("SelectItemsPage rendered")
    fun renderSelectItemsPage() {
        assertSelectItemsPageRendered()
    }

    @When("Tap on AddSelectionButton")
    fun tapOnFinishButton() {
        onNodeWithTag(SelectItemsPageTag.AddSelectionButton).performClick()
        waitForIdle()
    }
}

fun ComposeTestRule.assertSelectItemsPageRendered() {
    onNodeWithTag(SelectItemsPageTag.AppBar).assertIsDisplayed()
    onNodeWithTag(SelectItemsPageTag.SearchButton).assertIsDisplayed()
    onNodeWithTag(SelectItemsPageTag.LayoutButton).assertIsDisplayed()
    onNodeWithTag(SelectItemsPageTag.FilterButton).assertIsDisplayed()
    onNodeWithTag(SelectItemsPageTag.AddSelectionButton).assertIsDisplayed()
}

