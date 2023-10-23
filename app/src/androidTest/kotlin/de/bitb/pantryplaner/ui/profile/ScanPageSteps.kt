package de.bitb.pantryplaner.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.ScanPageTag
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@HiltAndroidTest
class ScanPageSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("ScanPage rendered")
    fun renderProfilePage() {
        onNodeWithTag(ScanPageTag.AppBar).assertIsDisplayed()
        onNodeWithTag(ScanPageTag.ScanLabel).assertIsDisplayed()
    }

    @When("Scan {string}")
    fun tapOnAddUserButton(scan: String) {
        scenarioData.scenario?.onActivity { activity ->
            val fragManger = activity.supportFragmentManager
            val navFrag = (fragManger.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
            val scanFrag = navFrag.childFragmentManager.fragments.first() as ScanFragment
            scanFrag.viewModel.onScan(scan)
        }
        waitForIdle()
    }

}

