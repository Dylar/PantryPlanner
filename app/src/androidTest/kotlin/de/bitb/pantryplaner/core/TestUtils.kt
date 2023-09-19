package de.bitb.pantryplaner.core

//import androidx.compose.ui.test.SemanticsNodeInteraction
//import androidx.compose.ui.test.junit4.AndroidComposeTestRule
//import androidx.compose.ui.test.onNodeWithTag
import androidx.annotation.StringRes
import androidx.test.platform.app.InstrumentationRegistry

fun getString(@StringRes id: Int, vararg args: Any): String {
    return InstrumentationRegistry.getInstrumentation().targetContext.resources.getString(id, *args)
}
