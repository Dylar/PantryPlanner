package de.bitb.pantryplaner.ui.base

sealed class TestTags {
    open val name: String
        get() = toString()
            .substringBefore("@")
            .substringAfterLast(".")
            .replace("$", ".")

    object SearchBar : TestTags()
    sealed class GridListLayout : TestTags() {
        object Grid : GridListLayout()
        object List : GridListLayout()
    }

    sealed class AddSubRow : TestTags() {
        object PlusButton : AddSubRow()
        object MinusButton : AddSubRow()
    }

    data class SelectItemHeader(val id: String) : TestTags() {
        override val name: String
            get() = super.name + ".$id"
    }

    sealed class ProfilePage : TestTags() {
        object AppBar : ProfilePage()
        object SettingsButton : ProfilePage()
        object QRInfo : ProfilePage()
        object QRLabel : ProfilePage()
        object ScanButton : ProfilePage()
    }

    sealed class ScanPage : TestTags() {
        object AppBar : ScanPage()
        object ScanLabel : ScanPage()
    }

    sealed class SettingsPage : TestTags() {
        object AppBar : SettingsPage()
        object InfoButton : SettingsPage()
    }

    sealed class LoginPage : TestTags() {
        object AppBar : LoginPage()
        object InfoButton : LoginPage()
        object EmailLabel : LoginPage()
        object PWLabel : LoginPage()
        object RegisterButton : LoginPage()
        object LoginButton : LoginPage()
        object ErrorLabel : LoginPage()
    }

    sealed class RegisterPage : TestTags() {
        object AppBar : RegisterPage()
        object EmailLabel : RegisterPage()
        object FirstNameLabel : RegisterPage()
        object LastNameLabel : RegisterPage()
        object PW1Label : RegisterPage()
        object PW2Label : RegisterPage()
        object RegisterButton : RegisterPage()
        object ErrorLabel : RegisterPage()
    }

    sealed class OverviewPage : TestTags() {
        object AppBar : OverviewPage()
        object ProfileButton : OverviewPage()
        object LayoutButton : OverviewPage()
        object AddButton : OverviewPage()
        object ToItemsButton : OverviewPage()
    }

    sealed class ItemsPage : TestTags() {
        object AppBar : ItemsPage()
        object SearchButton : ItemsPage()
        object LayoutButton : ItemsPage()
        object FilterButton : ItemsPage()
    }

    sealed class ChecklistPage : TestTags() {
        object AppBar : ChecklistPage()
        object LayoutButton : ChecklistPage()
        object FilterButton : ChecklistPage()
    }

    sealed class RefreshPage : TestTags() {
        object AppBar : RefreshPage()
        object LayoutButton : RefreshPage()
    }
}