package de.bitb.pantryplaner.ui.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.SettingsPageTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class SettingsPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("SettingsPage rendered")
    fun renderSettingsPage() {
        assertSettingsPageRendered()
    }

    @When("Tap on LogoutButton")
    fun tapOnLogoutButton() {
        onNodeWithTag(SettingsPageTag.LogoutButton).performClick()
        waitForIdle()
    }
}

fun ComposeTestRule.assertSettingsPageRendered() {
    onNodeWithTag(SettingsPageTag.AppBar).assertIsDisplayed()
    onNodeWithTag(SettingsPageTag.InfoButton).assertIsDisplayed()
    onNodeWithTag(SettingsPageTag.LogoutButton).assertIsDisplayed()
}