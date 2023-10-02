package de.bitb.pantryplaner.core

import androidx.annotation.StringRes
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import de.bitb.pantryplaner.ui.base.testTags.TestTag

inline fun <reified T> parsePOKO(fileName: String): T {
    val json = readJsonFromAssets(fileName)
    return Gson().fromJson(json, T::class.java)
}

fun readJsonFromAssets(fileName: String): String {
    return InstrumentationRegistry.getInstrumentation()
        .context
        .assets
        .open("jsons/$fileName.json")
        .bufferedReader()
        .use { it.readText() }
}

fun getString(@StringRes id: Int, vararg args: Any): String {
    return InstrumentationRegistry.getInstrumentation()
        .targetContext
        .resources
        .getString(id, *args)
}

fun ComposeTestRule.onNodeWithTag(
    testTag: TestTag,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction =
    onNodeWithTag(testTag.tagName, useUnmergedTree)

fun ComposeTestRule.hasTestTag(testTag: TestTag): SemanticsMatcher = hasTestTag(testTag.tagName)
fun SemanticsNodeInteraction.hasTextInHierarchy(targetText: String): SemanticsNodeInteraction {
    fun SemanticsNode.containsText(): Boolean {
        val hasRegularText = config.contains(SemanticsProperties.Text) &&
                config[SemanticsProperties.Text].any { it.text == targetText }
        val hasEditableText = config.contains(SemanticsProperties.EditableText) &&
                config[SemanticsProperties.EditableText].text == targetText

        return hasRegularText || hasEditableText
    }

    fun SemanticsNode.hasDescendantNodeWithText(): Boolean {
        if (containsText()) return true

        for (child in children) {
            if (child.hasDescendantNodeWithText()) {
                return true
            }
        }
        return false
    }

    val currentNode = fetchSemanticsNode()
    if (!currentNode.hasDescendantNodeWithText()) {
        throw AssertionError("Cannot find node or descendant with text '$targetText'")
    }
    return this
}
