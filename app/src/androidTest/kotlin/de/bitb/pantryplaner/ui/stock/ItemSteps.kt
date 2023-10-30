package de.bitb.pantryplaner.ui.stock

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.getString
import de.bitb.pantryplaner.core.hasTextInHierarchy
import de.bitb.pantryplaner.core.misc.formatted
import de.bitb.pantryplaner.core.onNodeWithParentTag
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.core.onNodeWithTextWithParentTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.AddSubRowTag
import de.bitb.pantryplaner.ui.base.testTags.ItemTag
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class ItemSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("No Items displayed")
    fun noItemsDisplayed() {
        onNodeWithText(getString(R.string.no_items)).assertIsDisplayed()
    }

    @Then("Item {string} in category {string} is displayed")
    fun itemInCategoryIsDisplayed(name: String, category: String) {
        onNodeWithTextWithParentTag(
            ItemTag(category, name),
            name,
            true,
        ).performScrollTo().assertIsDisplayed()
    }

    @Then("Item {string} in category {string} is NOT displayed")
    fun itemInCategoryIsNotDisplayed(name: String, category: String) {
        try {
            onNodeWithTag(ItemTag(category, name), true).assertIsNotDisplayed()
        } catch (_: AssertionError) {
            onNodeWithTag(ItemTag(category, name), true).assertDoesNotExist()
        }
    }

    @Then("Swipe to remove Item {string} in category {string}")
    fun swipeToRemoveItem(name: String, category: String) {
        onNodeWithTextWithParentTag(
            ItemTag(category, name),
            name,
            true,
        ).performTouchInput { swipeRight() }
        waitForIdle()
    }

    @When("LongPress on Item {string} in category {string}")
    fun longPressOnItemInCategory(name: String, category: String) {
        onNodeWithTextWithParentTag(
            ItemTag(category, name),
            name,
            true,
        ).performTouchInput { longClick() }
        waitForIdle()
    }

    @When("Tap on Item {string} in category {string}")
    fun tapOnItemInCategory(name: String, category: String) {
        onNodeWithTextWithParentTag(
            ItemTag(category, name),
            name,
            true,
        ).performClick()
        waitForIdle()
    }

    @Given("Item {string} in category {string} has amount {double}")
    fun itemHasAmount(name: String, category: String, amount: Double) {
        onNodeWithTag(ItemTag(category, name), true).hasTextInHierarchy(amount.formatted)
    }

    @When("Set Item {string} in category {string} amount to {string}")
    fun setItemAmountBy(name: String, category: String, input: String) {
        onNodeWithParentTag(
            ItemTag(category, name),
            AddSubRowTag.AmountText,
            true
        ).performTextReplacement(input)
        waitForIdle()
    }

    @When("Increase Item {string} in category {string} amount by {int}")
    fun increaseItemAmountBy(name: String, category: String, amount: Int) {
        for (i in 1..amount) {
            onNodeWithParentTag(
                ItemTag(category, name),
                AddSubRowTag.PlusButton,
                true
            ).performClick()
            waitForIdle()
        }
    }

    @When("Decrease Item {string} in category {string} amount by {int}")
    fun decreaseItemAmountBy(name: String, category: String, amount: Int) {
        for (i in 1..amount) {
            onNodeWithParentTag(
                ItemTag(category, name),
                AddSubRowTag.MinusButton,
                true
            ).performClick()
            waitForIdle()
        }
    }
}