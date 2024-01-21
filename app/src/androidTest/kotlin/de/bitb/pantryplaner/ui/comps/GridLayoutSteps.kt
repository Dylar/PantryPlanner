package de.bitb.pantryplaner.ui.comps

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.performTouchInput
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.core.onNodeWithTag
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.ui.base.testTags.CategoryTag
import io.cucumber.java.en.Given

@HiltAndroidTest
class GridLayoutSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Given("LongPress on Category {string}")
    fun longPressOnCategory(category: String) {
        onNodeWithTag(
            CategoryTag(category),
            useUnmergedTree = true
        ).performTouchInput { longClick() }
        waitForIdle()
    }

}