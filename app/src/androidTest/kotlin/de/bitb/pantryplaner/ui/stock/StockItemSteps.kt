package de.bitb.pantryplaner.ui.stock

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.getString
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.ItemTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class ItemSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("No Items displayed")
    fun noItemsDisplayed() {
        onNodeWithText(getString(R.string.no_items)).assertIsDisplayed()
        waitForIdle()
    }

    @Then("Item {string} in category {string} is displayed")
    fun itemInCategoryIsDisplayed(name: String, category: String) {
        onNodeWithTag(ItemTag(category, name), true).assertIsDisplayed()
        waitForIdle()
    }

    @Then("Item {string} in category {string} is NOT displayed")
    fun itemInCategoryIsNotDisplayed(name: String, category: String) {
        onNodeWithTag(ItemTag(category, name), true).assertDoesNotExist()
        waitForIdle()
    }

    @Then("Swipe to remove Item {string} in category {string}")
    fun swipeToRemoveItem(name: String, category: String) {
        onNodeWithTag(ItemTag(category, name), true).performTouchInput { swipeRight() }
        waitForIdle()
    }

    @When("LongPress on Item {string} in category {string}")
    fun longPressOnItemInCategory(name: String, category: String) {
        onNodeWithTag(ItemTag(category, name), true).performTouchInput { longClick() }
        waitForIdle()
    }
}