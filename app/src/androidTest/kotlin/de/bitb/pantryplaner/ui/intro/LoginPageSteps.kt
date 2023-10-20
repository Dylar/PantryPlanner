package de.bitb.pantryplaner.ui.intro

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.test.defaultPW
import de.bitb.pantryplaner.test.parseUser
import de.bitb.pantryplaner.ui.base.testTags.LoginPageTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class LoginPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("LoginPage rendered")
    fun renderLoginPage() = assertLoginPageRendered()

    @When("Login with email {string} and password {string}")
    fun loginUser(email: String, password: String) {
        loginUserWith(email, password)
    }

    @When("Login default User")
    fun loginDefaultUser() {
        val user = parseUser()
        loginUser(user.email, defaultPW)
    }

    @When("Tap on NaviToRegisterButton")
    fun tapRegisterButton() {
        onNodeWithTag(LoginPageTag.RegisterButton).performClick()
        waitForIdle()
    }

}

fun ComposeTestRule.assertLoginPageRendered() {
    onNodeWithTag(LoginPageTag.AppBar).assertIsDisplayed()
    onNodeWithTag(LoginPageTag.InfoButton).assertIsDisplayed()
    onNodeWithTag(LoginPageTag.EmailLabel).assertIsDisplayed()
    onNodeWithTag(LoginPageTag.PWLabel).assertIsDisplayed()
    onNodeWithTag(LoginPageTag.RegisterButton).assertIsDisplayed()
    onNodeWithTag(LoginPageTag.LoginButton).assertIsDisplayed()
    onNodeWithTag(LoginPageTag.ErrorLabel).assertDoesNotExist()
}

fun ComposeTestRule.loginUserWith(email: String, password: String) {
    onNodeWithTag(LoginPageTag.EmailLabel).performTextInput(email)
    onNodeWithTag(LoginPageTag.PWLabel).performTextInput(password)
    onNodeWithTag(LoginPageTag.LoginButton).performClick()
    waitForIdle()
}