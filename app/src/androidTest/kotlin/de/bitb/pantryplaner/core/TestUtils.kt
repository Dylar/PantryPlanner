package de.bitb.pantryplaner.core

import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.platform.app.InstrumentationRegistry
import de.bitb.pantryplaner.ui.base.testTags.TestTag

fun getString(@StringRes id: Int, vararg args: Any): String {
    return InstrumentationRegistry.getInstrumentation().targetContext.resources.getString(id, *args)
}

fun ComposeTestRule.onNodeWithTag(testTag: TestTag): SemanticsNodeInteraction =
    onNodeWithTag(testTag.tagName)
