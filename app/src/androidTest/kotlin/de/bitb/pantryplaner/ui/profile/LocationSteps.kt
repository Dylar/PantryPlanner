//package de.bitb.pantryplaner.ui.profile
//
//import androidx.compose.ui.test.*
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.test.core.app.launchActivity
//import de.bitb.pantryplaner.core.MainActivity
//import io.cucumber.java.en.Given
//import io.cucumber.java.en.Then
//import io.cucumber.java.en.When
//
//class LocationSteps {
//
//    // Create a rule for Compose testing
//    val composeTestRule = createComposeRule()
//
//    @Given("I am on the profile page")
//    fun i_am_on_the_profile_page() {
//        // Launch the activity containing your fragment with composables
//        launchActivity<MainActivity>()
//
//        // Check if a specific Composable (by contentDescription or testTag) is displayed
//        composeTestRule.onNodeWithContentDescription("Profile Description").assertIsDisplayed()
//    }
//
//    @When("I tap the {string} button")
//    fun i_tap_the_button(buttonName: String) {
//        when (buttonName) {
//            "New Location" -> composeTestRule.onNodeWithContentDescription("New Location Button").performClick()
//            "Create Location" -> composeTestRule.onNodeWithContentDescription("Create Location Button").performClick()
//            else -> throw IllegalArgumentException("Unknown button: $buttonName")
//        }
//    }
//
//    @Then("the {string} dialog should open")
//    fun the_dialog_should_open(dialogName: String) {
//        when (dialogName) {
//            "New Location" -> composeTestRule.onNodeWithContentDescription("New Location Dialog").assertIsDisplayed()
//            else -> throw IllegalArgumentException("Unknown dialog: $dialogName")
//        }
//    }
//
//    @When("I enter {string} as the location name")
//    fun i_enter_as_the_location_name(locationName: String) {
//        composeTestRule.onNodeWithContentDescription("Location Name Input").performTextInput(locationName)
//    }
//
//    @Then("{string} location should be shown in the profile")
//    fun location_should_be_shown_in_the_profile(locationName: String) {
//        composeTestRule.onNodeWithText(locationName).assertIsDisplayed()
//    }
//}
