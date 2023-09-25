package de.bitb.pantryplaner.ui.base.testTags

data class SelectItemHeaderTag(val name: String) : TestTag {
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

data class LocationItem(val name: String) : TestTag {
    override val tagName: String
        get() = super.tagName + ".$name"
}