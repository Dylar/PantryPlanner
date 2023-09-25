package de.bitb.pantryplaner.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.LocationItem
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class LocationSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("Location {string} should be shown")
    fun locationShouldBeShown(name: String) {
        onNodeWithTag(LocationItem(name), true).assertIsDisplayed()
        waitForIdle()
    }

    @Then("Location {string} should NOT be shown")
    fun locationShouldNotBeShown(name: String) {
        onNodeWithTag(LocationItem(name), true).assertDoesNotExist()
        waitForIdle()
    }

    @Then("Swipe to remove Location {string}")
    fun swipeToRemoveLocation(name: String) {
        //TODO do we need unmergedTree?
        onNodeWithTag(LocationItem(name), true)
            .performTouchInput { swipeRight() }
        waitForIdle()
    }

    @When("LongPress on Location {string}")
    fun longPressOnLocation(name: String) {
        onNodeWithTag(LocationItem(name), true)
            .performTouchInput { longClick() }
        waitForIdle()
    }
}