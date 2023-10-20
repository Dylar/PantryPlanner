package de.bitb.pantryplaner.core

import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import de.bitb.pantryplaner.ui.base.testTags.TestTag

fun ComposeTestRule.onNodeWithTag(
    testTag: TestTag,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction =
    onNodeWithTag(testTag.tagName, useUnmergedTree)

fun ComposeTestRule.onAllNodesWithTag(
    testTag: TestTag,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteractionCollection =
    onAllNodesWithTag(testTag.tagName, useUnmergedTree)

fun ComposeTestRule.hasTestTag(testTag: TestTag): SemanticsMatcher = hasTestTag(testTag.tagName)
fun SemanticsNodeInteraction.hasTextInHierarchy(text: String): SemanticsNodeInteraction {
    fun SemanticsNode.hasDescendantNodeWithText(): Boolean {
        if (assertTextInConfig(text)) return true

        for (child in children) {
            if (child.hasDescendantNodeWithText()) {
                return true
            }
        }
        return false
    }

    if (!fetchSemanticsNode().hasDescendantNodeWithText()) {
        throw AssertionError("Cannot find node or descendant with text '$text'")
    }
    return this
}

fun ComposeTestRule.onNodeWithTextWithParentTag(
    parentTag: TestTag,
    text: String,
    useUnmergedTree: Boolean = false,
): SemanticsNodeInteraction {
    val childNode = findDescendantNode(parentTag, useUnmergedTree) { assertTextInConfig(text) }
        ?: throw AssertionError("Cannot find a node with text '$text' inside an ancestor with test tag '${parentTag.tagName}'")

    return onNode(
        SemanticsMatcher("Custom matcher for $text")
        { it.id == childNode.id },
        useUnmergedTree = useUnmergedTree,
    )
}

fun ComposeTestRule.onNodeWithParentTag(
    parentTag: TestTag,
    childTag: TestTag,
    useUnmergedTree: Boolean = false,
): SemanticsNodeInteraction {
    val childNode =
        findDescendantNode(parentTag, useUnmergedTree) { assertTestTagInConfig(childTag) }
            ?: throw AssertionError("Cannot find a node with test tag '${childTag.tagName}' inside an ancestor with test tag '${parentTag.tagName}'")

    return onNode(
        SemanticsMatcher("Custom matcher for ${childTag.tagName}")
        { it.id == childNode.id },
        useUnmergedTree = useUnmergedTree,
    )
}

fun ComposeTestRule.assertNodeWithTextWithParentTagDoesNotExists(
    parentTag: TestTag,
    text: String,
    useUnmergedTree: Boolean = false,
) {
    val childNode = findDescendantNode(parentTag, useUnmergedTree) { assertTextInConfig(text) }
    if (childNode != null) {
        throw AssertionError("Found a node with text '${text}' inside an ancestor with test tag '${parentTag.tagName}'")
    }
}

fun ComposeTestRule.assertNodeWithParentTagDoesNotExists(
    parentTag: TestTag,
    childTag: TestTag,
    useUnmergedTree: Boolean = false,
) {
    val childNode =
        findDescendantNode(parentTag, useUnmergedTree) { assertTestTagInConfig(childTag) }
    if (childNode != null) {
        throw AssertionError("Found a node with test tag '${childTag.tagName}' inside an ancestor with test tag '${parentTag.tagName}'")
    }
}

private fun ComposeTestRule.findDescendantNode(
    parentTag: TestTag,
    useUnmergedTree: Boolean,
    isNode: SemanticsNode.() -> Boolean,
): SemanticsNode? {
    fun SemanticsNode.getDescendantNodeWithTestTag(): SemanticsNode? {
        if (isNode()) return this

        for (child in children) {
            val node = child.getDescendantNodeWithTestTag()
            if (node != null) {
                return node
            }
        }
        return null
    }

    return onNodeWithTag(parentTag, useUnmergedTree = useUnmergedTree)
        .fetchSemanticsNode()
        .getDescendantNodeWithTestTag()
}

private fun SemanticsNode.assertTestTagInConfig(testTag: TestTag): Boolean {
    return config.contains(SemanticsProperties.TestTag) &&
            config[SemanticsProperties.TestTag] == testTag.tagName
}

private fun SemanticsNode.assertTextInConfig(text: String): Boolean {
    val hasRegularText = config.contains(SemanticsProperties.Text) &&
            config[SemanticsProperties.Text].any { it.text == text }
    val hasEditableText = config.contains(SemanticsProperties.EditableText) &&
            config[SemanticsProperties.EditableText].text == text

    return hasRegularText || hasEditableText
}