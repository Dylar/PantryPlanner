package de.bitb.pantryplaner.ui.settings

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.TestTags
import io.cucumber.java.en.Then

@HiltAndroidTest
class SettingsPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("SettingsPage rendered")
    fun renderSettingsPage() {
        assertSettingsPageRendered()
    }

}

fun ComposeTestRule.assertSettingsPageRendered() {
    onNodeWithTag(TestTags.SettingsPage.AppBar.name).assertIsDisplayed()
    onNodeWithTag(TestTags.SettingsPage.InfoButton.name).assertIsDisplayed()
}