package de.bitb.pantryplaner.ui.base.testTags

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
    object NewChecklistButton : OverviewPageTag()
    object StockButton : OverviewPageTag()
}

sealed class StockPageTag : TestTag {
    object AppBar : StockPageTag()
    object SearchButton : StockPageTag()
    object LayoutButton : StockPageTag()
    object FilterButton : StockPageTag()
    object NewItemButton : StockPageTag()
}

sealed class ItemsPageTag : TestTag {
    object AppBar : ItemsPageTag()
    object SearchButton : ItemsPageTag()
    object LayoutButton : ItemsPageTag()
    object FilterButton : ItemsPageTag()
}

sealed class ChecklistPageTag : TestTag {
    object AppBar : ChecklistPageTag()
    object LayoutButton : ChecklistPageTag()
    object FilterButton : ChecklistPageTag()
    object AddItemButton : ChecklistPageTag()
    object FinishButton : ChecklistPageTag()
}

sealed class RefreshPageTag : TestTag {
    object AppBar : RefreshPageTag()
    object LayoutButton : RefreshPageTag()
}

sealed class ProfilePageTag : TestTag {
    object AppBar : ProfilePageTag()
    object SettingsButton : ProfilePageTag()
    object QRInfo : ProfilePageTag()
    object QRLabel : ProfilePageTag()
    object NewStockButton : ProfilePageTag()
    object ScanButton : ProfilePageTag()
}

sealed class ScanPageTag : TestTag {
    object AppBar : ScanPageTag()
    object ScanLabel : ScanPageTag()
}
