package de.bitb.pantryplaner.ui.recipes

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performTextReplacement
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.hasTextInHierarchy
import de.bitb.pantryplaner.core.onNodeWithParentTag
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.RecipeDetailsPageTag
import de.bitb.pantryplaner.ui.base.testTags.RecipeTag
import de.bitb.pantryplaner.ui.base.testTags.SearchDropDownTag
import de.bitb.pantryplaner.ui.tapOnFloatingActionButton
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class RecipeDetailsPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("RecipeDetailsPage rendered")
    fun renderRecipeDetailsPage() {
        onNodeWithTag(RecipeDetailsPageTag.RecipeDetailsPage).assertIsDisplayed()
        onNodeWithTag(RecipeDetailsPageTag.AppBar).assertIsDisplayed()
        onNodeWithTag(RecipeDetailsPageTag.LayoutButton).assertIsDisplayed()
        onNodeWithTag(RecipeDetailsPageTag.RecipeName).assertIsDisplayed()
    }

    @Then("Recipe is cookable")
    fun recipeIsCookable() {
        onNodeWithParentTag(
            RecipeDetailsPageTag.AppBar,
            RecipeTag.CookableIconTag,
            true,
        ).assertIsDisplayed()
        waitForIdle()
    }

    @Then("Recipe is NOT cookable")
    fun recipeIsNotCookable() {
        onNodeWithParentTag(
            RecipeDetailsPageTag.AppBar,
            RecipeTag.UncookableIconTag,
            true,
        ).assertIsDisplayed()
        waitForIdle()
    }

    @Then("Recipe name is {string}")
    fun recipeNameIs(name: String) {
        onNodeWithTag(RecipeDetailsPageTag.RecipeName)
            .hasTextInHierarchy(name)
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

    @When("Tap on RecipeDetailsPage AddItemButton")
    fun tapOnAddItemButton() {
        tapOnFloatingActionButton(RecipeDetailsPageTag.AddItemButton)
    }

    @And("Tap on CookButton")
    fun tapOnCookButton() {
        tapOnFloatingActionButton(RecipeDetailsPageTag.CookButton)
    }

    @And("Tap on BuyButton")
    fun tapOnBuyButton() {
        tapOnFloatingActionButton(RecipeDetailsPageTag.BuyButton)
    }

    @And("Tap on SaveRecipeButton")
    fun tapOnCreateRecipeButton() {
        tapOnFloatingActionButton(RecipeDetailsPageTag.SaveRecipeButton)
    }

    @And("SaveRecipeButton is NOT displayed")
    fun assertSaveRecipeButtonIsNotDisplayed() {
        onNodeWithTag(RecipeDetailsPageTag.SaveRecipeButton).assertDoesNotExist()
    }

    @And("CookButton is displayed")
    fun assertCookButtonIsDisplayed() {
        onNodeWithTag(RecipeDetailsPageTag.CookButton).assertIsDisplayed()
    }

    @And("CookButton is NOT displayed")
    fun assertCookButtonIsNotDisplayed() {
        onNodeWithTag(RecipeDetailsPageTag.CookButton).assertDoesNotExist()
    }
}
