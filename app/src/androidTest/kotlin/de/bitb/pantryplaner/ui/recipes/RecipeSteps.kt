package de.bitb.pantryplaner.ui.recipes

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.getString
import de.bitb.pantryplaner.core.onNodeWithParentTag
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.RecipeTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class RecipeSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("No Recipes displayed")
    fun noRecipesDisplayed() {
        onNodeWithText(getString(R.string.no_recipes)).assertIsDisplayed()
        waitForIdle()
    }

    @Then("Recipe {string} in category {string} is displayed")
    fun recipeIsDisplayed(name: String, category: String) {
        onNodeWithTag(RecipeTag(name, category), true).assertIsDisplayed()
        waitForIdle()
    }

    @Then("Recipe {string} in category {string} is NOT displayed")
    fun recipeIsNotDisplayed(name: String, category: String) {
        onNodeWithTag(RecipeTag(name, category), true).assertDoesNotExist()
        waitForIdle()
    }

    @Then("Recipe {string} in category {string} is cookable")
    fun recipeIsCookable(name: String, category: String) {
        onNodeWithParentTag(
            RecipeTag(name, category),
            RecipeTag.CookableIconTag,
            true,
        ).performScrollTo().assertIsDisplayed()
        waitForIdle()
    }

    @Then("Recipe {string} in category {string} is NOT cookable")
    fun recipeIsNotCookable(name: String, category: String) {
        onNodeWithParentTag(
            RecipeTag(name, category),
            RecipeTag.UncookableIconTag,
            true,
        ).performScrollTo().assertIsDisplayed()
        waitForIdle()
    }

    @When("Tap on Recipe {string} in category {string}")
    fun tapOnRecipe(name: String, category: String) {
        onNodeWithTag(RecipeTag(name, category), true).performClick()
        waitForIdle()
    }

    @Then("Swipe to remove Recipe {string} in category {string}")
    fun swipeToRemoveRecipe(name: String, category: String) {
        onNodeWithTag(RecipeTag(name, category), true)
            .performTouchInput { swipeRight() }
        waitForIdle()
    }

    @When("LongPress on Recipe {string} in category {string}")
    fun longPressOnRecipe(name: String, category: String) {
        onNodeWithTag(RecipeTag(name, category), true)
            .performTouchInput { longClick() }
        waitForIdle()
    }
}