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

    sealed class SettingsPage : TestTags() {
        object AppBar : SettingsPage()
        object InfoButton : SettingsPage()
    }

    sealed class OverviewPage : TestTags() {
        object AppBar : OverviewPage()
        object SettingsButton : OverviewPage()
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