package de.bitb.pantryplaner.ui.base.testTags

data class SelectItemHeaderTag(val name: String) : TestTag

data class SearchDropDownTag(val name: String) : TestTag
data class DropDownItemTag(val name: String) : TestTag

sealed class SharedWithTag : TestTag {
    object NothingShared : SharedWithTag()
    data class SharedChip(val name: String) : SharedWithTag()
}

data class UserTag(val fullName: String) : TestTag
data class StockTag(val name: String) : TestTag
data class ItemTag(val name: String, val category: String) : TestTag{
    object UnsharedIconTag : TestTag
}
data class ChecklistTag(val name: String) : TestTag
data class RecipeTag(val name: String, val category: String) : TestTag {
    object CookableIconTag : TestTag
    object UncookableIconTag : TestTag
}
