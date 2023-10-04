package de.bitb.pantryplaner.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.test.ScenarioData
import io.cucumber.java.en.Then


@HiltAndroidTest
class CommonSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("On back")
    fun onBack() {
        scenarioData.scenario?.onActivity { activity ->
            val fragManger = activity.supportFragmentManager
            val navFrag = (fragManger.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
            navFrag.navController.popBackStack()
        }
        waitForIdle()
    }

    @Then("SnackBar shown: {string}")
    fun snackBarShown(message: String) {
        onNodeWithText(message).assertIsDisplayed()
    }
}