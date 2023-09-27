package de.bitb.pantryplaner.core

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
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
