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

sealed class RecipesPageTag : TestTag {
    object AppBar : RecipesPageTag()
    object LayoutButton : RecipesPageTag()
    object NewRecipeButton : RecipesPageTag()
}

sealed class RecipeDetailsPageTag : TestTag {
    object RecipeDetailsPage : RecipeDetailsPageTag()
    object AppBar : RecipeDetailsPageTag()
    object DetailsButton : RecipeDetailsPageTag()
    object LayoutButton : RecipeDetailsPageTag()
    object RecipeName : RecipeDetailsPageTag()
    object CookButton : RecipeDetailsPageTag()
    object BuyButton : RecipeDetailsPageTag()
    object AddItemButton : RecipeDetailsPageTag()
    object SaveRecipeButton : RecipeDetailsPageTag()
}

sealed class StocksPageTag : TestTag {
    object AppBar : StocksPageTag()
    object SearchButton : StocksPageTag()
    object DetailsButton : StocksPageTag()
    object LayoutButton : StocksPageTag()
    object FilterButton : StocksPageTag()
    object NewStockButton : StocksPageTag()
    object NewItemButton : StocksPageTag()

    data class StockPage(val name: String) : StocksPageTag()
    data class StockTabTag(val name: String) : StocksPageTag()
}

//sealed class ItemDetailPageTag : TestTag { // TODO Item detail page
//    object AppBar : ItemDetailPageTag()
//}

sealed class ChecklistPageTag : TestTag {
    object ChecklistPage : ChecklistPageTag()
    object AppBar : ChecklistPageTag()
    object DetailsButton : ChecklistPageTag()
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
