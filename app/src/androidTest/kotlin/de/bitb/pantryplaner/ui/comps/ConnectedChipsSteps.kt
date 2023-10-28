package de.bitb.pantryplaner.ui.comps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.getParentTag
import de.bitb.pantryplaner.core.onNodeWithParentTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.SharedWithTag
import io.cucumber.java.en.Then

@HiltAndroidTest
class ConnectedChipsSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Then("{string} shared with none")
    fun sharedWithNone(parent: String) {
        onNodeWithParentTag(
            getParentTag(parent),
            SharedWithTag.NothingShared,
        ).assertIsDisplayed()
    }

    @Then("{string} shared with {string}")
    fun stockDialogSharedWith(parent: String, name: String) {
        onNodeWithParentTag(
            getParentTag(parent),
            SharedWithTag.SharedChip(name),
        ).assertIsDisplayed()
    }

    @Then("{string} unshare with {string}")
    fun stockDialogUnsharedWith(parent: String, name: String) {
        onNodeWithParentTag(
            getParentTag(parent),
            SharedWithTag.SharedChip(name),
        ).performClick()
    }
}
