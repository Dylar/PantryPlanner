package de.bitb.pantryplaner.ui.SelectItems

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.FloatingExpandingButtonTag
import de.bitb.pantryplaner.ui.base.testTags.SelectItemsPageTag
import de.bitb.pantryplaner.ui.tapOnFloatingActionButton
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

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
        tapOnFloatingActionButton(SelectItemsPageTag.AddSelectionButton)
    }

    @When("Tap on SelectItemsPage NewItemButton")
    fun tapOnNewItemButton() {
        tapOnFloatingActionButton(SelectItemsPageTag.AddItemButton)
    }
}

fun ComposeTestRule.assertSelectItemsPageRendered() {
    onNodeWithTag(SelectItemsPageTag.AppBar).assertIsDisplayed()
    onNodeWithTag(SelectItemsPageTag.SearchButton).assertIsDisplayed()
    onNodeWithTag(SelectItemsPageTag.LayoutButton).assertIsDisplayed()
    onNodeWithTag(FloatingExpandingButtonTag).assertIsDisplayed()
}

