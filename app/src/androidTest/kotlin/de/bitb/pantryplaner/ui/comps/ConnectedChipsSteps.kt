package de.bitb.pantryplaner.ui.comps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.getParentTag
import de.bitb.pantryplaner.core.onNodeWithParentTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.AddEditChecklistDialogTag
import de.bitb.pantryplaner.ui.base.testTags.AddEditItemDialogTag
import de.bitb.pantryplaner.ui.base.testTags.AddEditStockDialogTag
import de.bitb.pantryplaner.ui.base.testTags.ChecklistPageTag
import de.bitb.pantryplaner.ui.base.testTags.SharedWithTag
import de.bitb.pantryplaner.ui.base.testTags.StockPageTag
import de.bitb.pantryplaner.ui.base.testTags.TestTag
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
}
