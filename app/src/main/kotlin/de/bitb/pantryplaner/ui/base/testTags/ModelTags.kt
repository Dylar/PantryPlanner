package de.bitb.pantryplaner.ui.base.testTags

data class SelectItemHeaderTag(val name: String) : TestTag {
    override val tagName: String
        get() = super.tagName + ".$name"
}

data class SearchDropDownTag(val name: String) : TestTag {
    override val tagName: String
        get() = super.tagName + ".$name"
}

data class DropDownItemTag(val name: String) : TestTag {
    override val tagName: String
        get() = super.tagName + ".$name"
}

sealed class SharedWithTag : TestTag {
    object NothingShared : SharedWithTag()
    data class SharedChip(val name: String) : SharedWithTag() {
        override val tagName: String
            get() = super.tagName + ".$name"
    }
}

data class StockTag(val name: String) : TestTag {
    override val tagName: String
        get() = super.tagName + ".$name"
}

data class ItemTag(val category: String, val name: String) : TestTag {
    override val tagName: String
        get() = super.tagName + ".$category.$name"
}