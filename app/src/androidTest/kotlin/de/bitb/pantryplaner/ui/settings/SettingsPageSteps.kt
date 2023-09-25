package de.bitb.pantryplaner.ui.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.SettingsPageTag
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
    onNodeWithTag(SettingsPageTag.AppBar).assertIsDisplayed()
    onNodeWithTag(SettingsPageTag.InfoButton).assertIsDisplayed()
}