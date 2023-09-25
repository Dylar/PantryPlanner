package de.bitb.pantryplaner.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.ProfilePageTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.java.en.And
import de.bitb.pantryplaner.ui.base.testTags.ConfirmDialogTag
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@HiltAndroidTest
class ProfilePageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("ProfilePage rendered")
    fun renderProfilePage() {
        assertProfilePageRendered()
    }

    @When("Tap on SettingsButton")
    fun tapOnSettingsButtonStep() {
        tapOnSettingsButton()
    }

    @When("Tap NewLocationButton on ProfilePage")
    fun tapNewLocationButtonOnProfilePage() {
        onNodeWithTag(ProfilePageTag.NewLocationButton).performClick()
        waitForIdle()
    }

    @Then("Location with name {string} should be shown on ProfilePage")
    fun locationWithNameShouldBeShownOnProfilePage(name: String) {
        onNodeWithTag(ProfilePageTag.LocationItem(name), true).assertIsDisplayed()
        waitForIdle()
    }

    @Then("Location with name {string} should NOT be shown on ProfilePage")
    fun locationWithNameShouldNotBeShownOnProfilePage(name: String) {
        onNodeWithTag(ProfilePageTag.LocationItem(name), true).assertDoesNotExist()
        waitForIdle()
    }

    @Then("Swipe to remove Location {string} on ProfilePage")
    fun swipeToRemoveLocationOnProfilePage(name: String) {
        //TODO do we need unmergedTree?
        onNodeWithTag(ProfilePageTag.LocationItem(name),true)
            .performTouchInput { swipeRight() }
        waitForIdle()
    }

    @And("Tap on confirm deletion")
    fun tapOnConfirmDeletion() {
        onNodeWithTag(ConfirmDialogTag.ConfirmButton).performClick()
        waitForIdle()
    }
}

fun ComposeTestRule.assertProfilePageRendered() {
    onNodeWithTag(ProfilePageTag.AppBar).assertIsDisplayed()
    onNodeWithTag(ProfilePageTag.SettingsButton).assertIsDisplayed()
    onNodeWithTag(ProfilePageTag.QRInfo).assertIsDisplayed()
    onNodeWithTag(ProfilePageTag.QRLabel).assertIsDisplayed()
    onNodeWithTag(ProfilePageTag.NewLocationButton).assertIsDisplayed()
    onNodeWithTag(ProfilePageTag.ScanButton).assertIsDisplayed()
}

fun ComposeTestRule.tapOnSettingsButton() {
    onNodeWithTag(ProfilePageTag.SettingsButton).performClick()
    waitForIdle()
}

