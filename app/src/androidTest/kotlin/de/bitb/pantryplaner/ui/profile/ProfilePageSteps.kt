package de.bitb.pantryplaner.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import cucumber.api.PendingException
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.TestTags
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

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
        // Write code here that turns the phrase above into concrete actions
        throw PendingException()
    }

    @Then("NewLocationDialog is displayed")
    fun newLocationDialogIsDisplayed() {
        // Write code here that turns the phrase above into concrete actions
        throw PendingException()
    }

    @And("Input {string} as Location name")
    fun inputAsLocationName(input: String) {
        // Write code here that turns the phrase above into concrete actions
        throw PendingException()
    }

    @And("Tap CreateLocationButton on ProfilePage")
    fun tapCreateLocationButtonOnProfilePage() {
        // Write code here that turns the phrase above into concrete actions
        throw PendingException()
    }

    @Then("Location with name {string} should be shown on ProfilePage")
    fun locationWithNameShouldBeShownOnProfilePage(name: String) {
        // Write code here that turns the phrase above into concrete actions
        throw PendingException()
    }
}

fun ComposeTestRule.assertProfilePageRendered() {
    onNodeWithTag(TestTags.ProfilePage.AppBar.name).assertIsDisplayed()
    onNodeWithTag(TestTags.ProfilePage.SettingsButton.name).assertIsDisplayed()
    onNodeWithTag(TestTags.ProfilePage.QRInfo.name).assertIsDisplayed()
    onNodeWithTag(TestTags.ProfilePage.QRLabel.name).assertIsDisplayed()
    onNodeWithTag(TestTags.ProfilePage.ScanButton.name).assertIsDisplayed()
}

fun ComposeTestRule.tapOnSettingsButton() {
    onNodeWithTag(TestTags.ProfilePage.SettingsButton.name).performClick()
    waitForIdle()
}