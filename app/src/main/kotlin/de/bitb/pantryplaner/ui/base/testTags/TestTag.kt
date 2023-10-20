package de.bitb.pantryplaner.ui.base.testTags

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

interface TestTag {
    val tagName: String
        get() = toString()
            .substringBefore("@")
            .substringAfterLast(".")
            .replace("$", ".")
            .replace("TestTags.", "")
}

fun Modifier.testTag(testTag: TestTag): Modifier = testTag(testTag.tagName)
