package de.bitb.pantryplaner.ui.comps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.AddEditStockDialogTag
import de.bitb.pantryplaner.ui.base.testTags.SearchDropDownTag
import de.bitb.pantryplaner.ui.base.testTags.SharedWithTag
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import cucumber.api.PendingException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@HiltAndroidTest
class ConnectedChipsSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("Shared with none")
    fun sharedWithNone() {
        onNodeWithTag(SharedWithTag.NothingShared).assertIsDisplayed()
    }

    @Then("Shared with {string}")
    fun sharedWith(name: String) {
        onNodeWithTag(SharedWithTag.SharedChip(name)).assertIsDisplayed()
    }
}