package de.bitb.pantryplaner.ui.overview

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.OverviewPageTag
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
    onNodeWithTag(OverviewPageTag.AppBar).assertIsDisplayed()
    onNodeWithTag(OverviewPageTag.ProfileButton).assertIsDisplayed()
    onNodeWithTag(OverviewPageTag.LayoutButton).assertIsDisplayed()
    onNodeWithTag(OverviewPageTag.AddButton).assertIsDisplayed()
    onNodeWithTag(OverviewPageTag.ToItemsButton).assertIsDisplayed()
}

fun ComposeTestRule.tapOnProfileButton() {
    onNodeWithTag(OverviewPageTag.ProfileButton).performClick()
    waitForIdle()
}
