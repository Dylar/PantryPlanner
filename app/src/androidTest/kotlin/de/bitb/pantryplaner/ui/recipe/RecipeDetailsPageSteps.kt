package de.bitb.pantryplaner.ui.recipe

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
import de.bitb.pantryplaner.ui.base.testTags.RecipeDetailsPageTag
import de.bitb.pantryplaner.ui.base.testTags.SearchDropDownTag
import io.cucumber.java.en.And
import io.cucumber.java.en.Then

@HiltAndroidTest
class RecipeDetailsPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("RecipeDetailsPage rendered")
    fun renderRecipeDetailsPage() {
        assertRecipeDetailsPageRendered()
    }

    @Then("Recipe name is {string}")
    fun itemRecipeIs(name: String) {
        onNodeWithParentTag(
            AddEditItemDialogTag.DialogTag,
            AddEditItemDialogTag.NameLabel
        ).hasTextInHierarchy(name)
            .assertIsDisplayed()
    }

    @Then("Recipe category is {string}")
    fun recipeCategoryIs(name: String) {
        onNodeWithTag(SearchDropDownTag("Kategorie"))
            .hasTextInHierarchy(name)
            .assertIsDisplayed()
    }

    @And("Input {string} as Recipe name")
    fun inputAsRecipeName(input: String) {
        onNodeWithTag(RecipeDetailsPageTag.RecipeName).performTextReplacement(input)
        waitForIdle()
    }

    @And("Input {string} as Recipe category")
    fun inputAsRecipeCategory(input: String) {
        onNodeWithTag(SearchDropDownTag("Kategorie")).performTextReplacement(input)
        waitForIdle()
    }

    @And("Tap on SaveRecipeButton")
    fun tapOnCreateItemButton() {
        onNodeWithTag(RecipeDetailsPageTag.SaveRecipeButton).performClick()
        waitForIdle()
    }
}

fun ComposeTestRule.assertRecipeDetailsPageRendered() {
    onNodeWithTag(RecipeDetailsPageTag.AppBar).assertIsDisplayed()
//    onNodeWithTag(RecipePageTag.SearchButton).assertIsDisplayed() // TODO is search needed?
    onNodeWithTag(RecipeDetailsPageTag.LayoutButton).assertIsDisplayed()
//    onNodeWithTag(FloatingExpandingButtonTag).assertIsDisplayed() // TODO if more needed
}

