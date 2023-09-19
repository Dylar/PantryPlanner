package de.bitb.pantryplaner.ui.intro

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.test.buildUser
import de.bitb.pantryplaner.test.defaultPW
import de.bitb.pantryplaner.ui.base.TestTags
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class LoginPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("LoginPage rendered")
    fun renderLoginPage() = assertLoginPageRendered()

    @When("I login with email {string} and password {string}")
    fun loginUser(email: String, password: String) {
        loginUserWith(email, password)
    }

    @When("Login default User")
    fun loginDefaultUserStep() {
        loginDefaultUser()
    }
}

fun ComposeTestRule.assertLoginPageRendered() {
    onNodeWithTag(TestTags.LoginPage.AppBar.name).assertIsDisplayed()
    onNodeWithTag(TestTags.LoginPage.InfoButton.name).assertIsDisplayed()
    onNodeWithTag(TestTags.LoginPage.EmailLabel.name).assertIsDisplayed()
    onNodeWithTag(TestTags.LoginPage.PWLabel.name).assertIsDisplayed()
    onNodeWithTag(TestTags.LoginPage.RegisterButton.name).assertIsDisplayed()
    onNodeWithTag(TestTags.LoginPage.LoginButton.name).assertIsDisplayed()
    onNodeWithTag(TestTags.LoginPage.ErrorLabel.name).assertDoesNotExist()
}

fun ComposeTestRule.loginUserWith(email: String, password: String) {
    onNodeWithTag(TestTags.LoginPage.EmailLabel.name).performTextInput(email)
    onNodeWithTag(TestTags.LoginPage.PWLabel.name).performTextInput(password)
    onNodeWithTag(TestTags.LoginPage.LoginButton.name).performClick()
    waitForIdle()
}

fun ComposeTestRule.loginDefaultUser() {
    val defaultUser = buildUser()
    loginUserWith(defaultUser.email, defaultPW)
}