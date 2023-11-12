package de.bitb.pantryplaner.ui.base.testTags

object SearchBarTag : TestTag

object GridLayoutTag : TestTag
object ListLayoutTag : TestTag

sealed class AddSubRowTag : TestTag {
    object PlusButton : AddSubRowTag()
    object AmountText : AddSubRowTag()
    object MinusButton : AddSubRowTag()
}

object UnsharedIconTag : TestTag

sealed class BottomNaviTag : TestTag {
    object ChecklistsButton : BottomNaviTag()
    object StocksButton : BottomNaviTag()
    object RecipesButton : BottomNaviTag()
    object ProfileButton : BottomNaviTag()
    object SettingsButton : BottomNaviTag()
}

object FloatingExpandingButtonTag : TestTag