package de.bitb.pantryplaner.ui.base.testTags

sealed class SettingsPageTag : TestTag {
    object AppBar : SettingsPageTag()
    object InfoButton : SettingsPageTag()
    object LogoutButton : SettingsPageTag()
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

sealed class ChecklistsPageTag : TestTag {
    object AppBar : ChecklistsPageTag()
    object LayoutButton : ChecklistsPageTag()
    object NewChecklistButton : ChecklistsPageTag()
}

sealed class StocksPageTag : TestTag {
    object AppBar : StocksPageTag()
    object SearchButton : StocksPageTag()
    object LayoutButton : StocksPageTag()
    object FilterButton : StocksPageTag()
    object NewStockButton : StocksPageTag()
    object NewItemButton : StocksPageTag()

    data class StockPage(val name: String) : StocksPageTag()
    data class StockTabTag(val name: String) : TestTag
}

sealed class ChecklistPageTag : TestTag {
    object ChecklistPage : ChecklistPageTag()
    object AppBar : ChecklistPageTag()
    object LayoutButton : ChecklistPageTag()
    object FilterButton : ChecklistPageTag()
    object AddItemButton : ChecklistPageTag()
    object FinishButton : ChecklistPageTag()
}

sealed class SelectItemsPageTag : TestTag {
    object AppBar : SelectItemsPageTag()
    object SearchButton : SelectItemsPageTag()
    object LayoutButton : SelectItemsPageTag()
    object FilterButton : SelectItemsPageTag()
    object AddItemButton : SelectItemsPageTag()
    object AddSelectionButton : SelectItemsPageTag()
}

sealed class RefreshPageTag : TestTag {
    object AppBar : RefreshPageTag()
    object LayoutButton : RefreshPageTag()
}

sealed class ProfilePageTag : TestTag {
    object AppBar : ProfilePageTag()
    object QRInfo : ProfilePageTag()
    object QRLabel : ProfilePageTag()
    object NewStockButton : ProfilePageTag()
    object AddUserButton : ProfilePageTag()
}

sealed class ScanPageTag : TestTag {
    object AppBar : ScanPageTag()
    object ScanLabel : ScanPageTag()
}
