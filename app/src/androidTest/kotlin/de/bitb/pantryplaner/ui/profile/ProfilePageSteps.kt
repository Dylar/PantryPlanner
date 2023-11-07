package de.bitb.pantryplaner.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.assertBottomNaviBar
import de.bitb.pantryplaner.ui.base.testTags.ProfilePageTag
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

    @When("Tap on ProfilePage NewStockButton")
    fun tapOnNewStockButton() {
        onNodeWithTag(ProfilePageTag.NewStockButton).performClick()
        waitForIdle()
    }

    @When("Tap on AddUserButton")
    fun tapOnAddUserButton() {
        onNodeWithTag(ProfilePageTag.AddUserButton).performClick()
        waitForIdle()
    }

}

fun ComposeTestRule.assertProfilePageRendered() {
    onNodeWithTag(ProfilePageTag.AppBar).assertIsDisplayed()
    onNodeWithTag(ProfilePageTag.QRInfo).assertIsDisplayed()
    onNodeWithTag(ProfilePageTag.QRLabel).assertIsDisplayed()
    onNodeWithTag(ProfilePageTag.NewStockButton).assertIsDisplayed()
    onNodeWithTag(ProfilePageTag.AddUserButton).assertIsDisplayed()
    assertBottomNaviBar()
}

