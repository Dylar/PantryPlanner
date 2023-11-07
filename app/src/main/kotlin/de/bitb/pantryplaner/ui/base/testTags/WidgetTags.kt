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
    object OverviewButton : BottomNaviTag()
    object StockButton : BottomNaviTag()
    object ProfileButton : BottomNaviTag()
    object SettingsButton : BottomNaviTag()
}
