package de.bitb.pantryplaner.ui.intro

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.RegisterPageTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class RegisterPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("RegisterPage rendered")
    fun renderRegisterPage() {
        onNodeWithTag(RegisterPageTag.AppBar).assertIsDisplayed()
        onNodeWithTag(RegisterPageTag.EmailLabel).assertIsDisplayed()
        onNodeWithTag(RegisterPageTag.FirstNameLabel).assertIsDisplayed()
        onNodeWithTag(RegisterPageTag.LastNameLabel).assertIsDisplayed()
        onNodeWithTag(RegisterPageTag.PW1Label).assertIsDisplayed()
        onNodeWithTag(RegisterPageTag.PW2Label).assertIsDisplayed()
        onNodeWithTag(RegisterPageTag.RegisterButton).assertIsDisplayed()
        onNodeWithTag(RegisterPageTag.ErrorLabel).assertDoesNotExist()
    }

    @When("Register with first name {string}, last name {string}, email {string} and password {string}")
    fun registerUser(firstName: String, lastName: String, email: String, password: String) {
        onNodeWithTag(RegisterPageTag.FirstNameLabel).performTextInput(firstName)
        onNodeWithTag(RegisterPageTag.LastNameLabel).performTextInput(lastName)
        onNodeWithTag(RegisterPageTag.EmailLabel).performTextInput(email)
        onNodeWithTag(RegisterPageTag.PW1Label).performTextInput(password)
        onNodeWithTag(RegisterPageTag.PW2Label).performTextInput(password)
        onNodeWithTag(RegisterPageTag.RegisterButton).performClick()
        waitForIdle()
    }

}