package de.bitb.pantryplaner.ui.overview

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.TestTags
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class OverviewPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("OverviewPage rendered")
    fun renderOverviewPage() = assertOverviewPageRendered()

    @When("Tap on ProfileButton")
    fun tapOnProfileButtonStep() {
        tapOnProfileButton()
    }
}

fun ComposeTestRule.assertOverviewPageRendered() {
    onNodeWithTag(TestTags.OverviewPage.AppBar.name).assertIsDisplayed()
    onNodeWithTag(TestTags.OverviewPage.ProfileButton.name).assertIsDisplayed()
    onNodeWithTag(TestTags.OverviewPage.LayoutButton.name).assertIsDisplayed()
    onNodeWithTag(TestTags.OverviewPage.AddButton.name).assertIsDisplayed()
    onNodeWithTag(TestTags.OverviewPage.ToItemsButton.name).assertIsDisplayed()
}

fun ComposeTestRule.tapOnProfileButton() {
    onNodeWithTag(TestTags.OverviewPage.ProfileButton.name).performClick()
    waitForIdle()
}
