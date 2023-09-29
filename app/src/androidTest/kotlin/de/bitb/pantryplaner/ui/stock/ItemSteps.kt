package de.bitb.pantryplaner.ui.stock

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.ItemTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class ItemSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("Item {string} is displayed")
    fun itemIsDisplayed(name: String) {
        onNodeWithTag(ItemTag(name), true).assertIsDisplayed()
        waitForIdle()
    }

    @Then("Item {string} is NOT displayed")
    fun itemIsNotDisplayed(name: String) {
        onNodeWithTag(ItemTag(name), true).assertDoesNotExist()
        waitForIdle()
    }

    @Then("Swipe to remove Item {string}")
    fun swipeToRemoveItem(name: String) {
        //TODO do we need unmergedTree?
        onNodeWithTag(ItemTag(name), true)
            .performTouchInput { swipeRight() }
        waitForIdle()
    }

    @When("LongPress on Item {string}")
    fun longPressOnItem(name: String) {
        onNodeWithTag(ItemTag(name), true)
            .performTouchInput { longClick() }
        waitForIdle()
    }
}