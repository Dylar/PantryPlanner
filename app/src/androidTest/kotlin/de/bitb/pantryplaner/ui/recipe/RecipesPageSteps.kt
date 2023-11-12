package de.bitb.pantryplaner.ui.recipe

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.RecipesPageTag
import de.bitb.pantryplaner.ui.tapOnFloatingActionButton
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class RecipePageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("RecipesPage rendered")
    fun renderRecipesPage() {
        assertRecipesPageRendered()
    }

    @When("Tap on NewRecipeButton")
    fun tapOnNewStockButton() {
        tapOnFloatingActionButton(RecipesPageTag.NewRecipeButton)
    }
}

fun ComposeTestRule.assertRecipesPageRendered() {
    onNodeWithTag(RecipesPageTag.AppBar).assertIsDisplayed()
//    onNodeWithTag(RecipePageTag.SearchButton).assertIsDisplayed() // TODO is search needed?
    onNodeWithTag(RecipesPageTag.LayoutButton).assertIsDisplayed()
    onNodeWithTag(RecipesPageTag.NewRecipeButton).assertIsDisplayed()
//    onNodeWithTag(FloatingExpandingButtonTag).assertIsDisplayed() // TODO if more needed
}

