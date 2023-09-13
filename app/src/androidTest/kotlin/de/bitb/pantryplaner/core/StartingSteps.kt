package de.bitb.pantryplaner.core

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.launchActivity
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.ui.intro.loginUserWith
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import org.junit.Rule

@HiltAndroidTest
class StartingSteps {

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

//    private lateinit var scenario: Scenario
//
//    @Before
//    fun setUp(scenario: Scenario) {
//        this.scenario = scenario
//    }

    @Given("Run App")
    fun runApp() = composeRule.apply {
        launchActivity<MainActivity>()
        waitForIdle()
    }

    @Given("Login default user")
    fun loginDefaultUser() = composeRule.apply {
        runApp()
        loginUserWith("defaul@user.de", "1Password!")
    }

}