package de.bitb.pantryplaner.ui.intro

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.MainActivity
import de.bitb.pantryplaner.ui.base.TestTags
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import org.junit.Rule

@HiltAndroidTest
class LoginPageSteps {

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

//    private lateinit var scenario: Scenario
//
//    @Before
//    fun setUp(scenario: Scenario) {
//        this.scenario = scenario
//    }

    @Given("Login page rendered")
    fun renderLoginPage(email: String, password: String) = composeRule.apply {
        onNodeWithTag(TestTags.LoginPage.AppBar.name).assertIsDisplayed()
        onNodeWithTag(TestTags.LoginPage.InfoButton.name).assertIsDisplayed()
        onNodeWithTag(TestTags.LoginPage.EmailLabel.name).assertIsDisplayed()
        onNodeWithTag(TestTags.LoginPage.PWLabel.name).assertIsDisplayed()
        onNodeWithTag(TestTags.LoginPage.RegisterButton.name).assertIsDisplayed()
        onNodeWithTag(TestTags.LoginPage.LoginButton.name).assertIsDisplayed()
        onNodeWithTag(TestTags.LoginPage.ErrorLabel.name).assertIsNotDisplayed()
    }

    @Given("Login user with {email} and {password}")
    fun loginUser(email: String, password: String) = composeRule.apply {
        loginUserWith(email, password)
    }
}

fun ComposeTestRule.loginUserWith(email: String, password: String) {
    onNodeWithTag(TestTags.LoginPage.EmailLabel.name).performTextInput(email)
    onNodeWithTag(TestTags.LoginPage.PWLabel.name).performTextInput(password)
    onNodeWithTag(TestTags.LoginPage.LoginButton.name).performClick()
    waitForIdle()
    onNodeWithTag(TestTags.OverviewPage.AppBar.name).assertIsDisplayed()
}
