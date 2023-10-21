package de.bitb.pantryplaner.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.getString
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.UserTag
import io.cucumber.java.en.Then

@HiltAndroidTest
class UserSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("No connected Users displayed")
    fun noStocksDisplayed() {
        onNodeWithText(getString(R.string.no_connected_users)).assertIsDisplayed()
        waitForIdle()
    }

    @Then("User {string} is displayed")
    fun userIsDisplayed(name: String) {
        onNodeWithTag(UserTag(name), true).assertIsDisplayed()
        waitForIdle()
    }

    @Then("User {string} is NOT displayed")
    fun userIsNotDisplayed(name: String) {
        onNodeWithTag(UserTag(name), true).assertDoesNotExist()
        waitForIdle()
    }

    @Then("Swipe to remove User {string}")
    fun swipeToRemoveUser(name: String) {
        onNodeWithTag(UserTag(name), true)
            .performTouchInput { swipeRight() }
        waitForIdle()
    }
}