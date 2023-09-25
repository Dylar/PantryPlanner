package de.bitb.pantryplaner.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.test.ScenarioData
import io.cucumber.java.en.Given

@HiltAndroidTest
class SnackBarSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Given("SnackBar shown: {string}")
    fun snackBarShown(message: String) {
        onNodeWithText(message).assertIsDisplayed()
    }
}