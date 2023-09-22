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

sealed class SettingsPageTag : TestTag {
    object AppBar : SettingsPageTag()
    object InfoButton : SettingsPageTag()
}

sealed class LoginPageTag : TestTag {
    object AppBar : LoginPageTag()
    object InfoButton : LoginPageTag()
    object EmailLabel : LoginPageTag()
    object PWLabel : LoginPageTag()
    object RegisterButton : LoginPageTag()
    object LoginButton : LoginPageTag()
    object ErrorLabel : LoginPageTag()
}

sealed class RegisterPageTag : TestTag {
    object AppBar : RegisterPageTag()
    object EmailLabel : RegisterPageTag()
    object FirstNameLabel : RegisterPageTag()
    object LastNameLabel : RegisterPageTag()
    object PW1Label : RegisterPageTag()
    object PW2Label : RegisterPageTag()
    object RegisterButton : RegisterPageTag()
    object ErrorLabel : RegisterPageTag()
}

sealed class OverviewPageTag : TestTag {
    object AppBar : OverviewPageTag()
    object ProfileButton : OverviewPageTag()
    object LayoutButton : OverviewPageTag()
    object AddButton : OverviewPageTag()
    object ToItemsButton : OverviewPageTag()
}

sealed class ItemsPageTag : TestTag {
    object AppBar : ItemsPageTag()
    object SearchButton : ItemsPageTag()
    object LayoutButton : ItemsPageTag()
    object FilterButton : ItemsPageTag()
    //TODO add/edit dialog
}

sealed class ChecklistPageTag : TestTag {
    object AppBar : ChecklistPageTag()
    object LayoutButton : ChecklistPageTag()
    object FilterButton : ChecklistPageTag()
    object SharedWithLabel : ChecklistPageTag()
}

sealed class RefreshPageTag : TestTag {
    object AppBar : RefreshPageTag()
    object LayoutButton : RefreshPageTag()
}