//package de.bitb.pantryplaner.core
//
//import androidx.compose.ui.test.assertIsDisplayed
//import androidx.compose.ui.test.junit4.createAndroidComposeRule
//import androidx.compose.ui.test.onNodeWithTag
//import androidx.compose.ui.test.performClick
//import androidx.compose.ui.test.performTextInput
//import androidx.test.core.app.launchActivity
//import dagger.hilt.android.testing.HiltAndroidRule
//import de.bitb.pantryplaner.ui.base.TestTags
//import io.cucumber.java.Scenario
//import io.cucumber.java.en.Given
//import org.junit.Before
//import org.junit.Rule
//
//class NavigationSteps {
//
//    @get:Rule(order = 0)
//    val hiltRule = HiltAndroidRule(this)
//
//    @get:Rule(order = 1)
//    val composeRule = createAndroidComposeRule<MainActivity>()
//
//    private lateinit var currentScenario: Scenario
//
//    @Before
//    fun setUp(scenario: Scenario) {
//        currentScenario = scenario
//        hiltRule.inject()
//    }
//
//    @Given("Login default user")
//    fun loginUser() {
//        loginUser("defaul@user.de", "Pw")
//    }
//
//    @Given("Login user with {email} and {password}")
//    fun loginUser(email: String, password: String) {
//        composeRule.apply {
//            onNodeWithTag(TestTags.LoginPage.EmailLabel.name).performTextInput("email@user.de")
//            onNodeWithTag(TestTags.LoginPage.PWLabel.name).performTextInput("pw")
//            onNodeWithTag(TestTags.LoginPage.LoginButton.name).performClick()
//            waitForIdle()
//            onNodeWithTag(TestTags.OverviewPage.AppBar.name).assertIsDisplayed()
//        }
//    }
//}