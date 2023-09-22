package de.bitb.pantryplaner.ui.base.testTags

object SearchBarTag : TestTag

object GridLayoutTag : TestTag
object ListLayoutTag : TestTag

sealed class AddSubRowTag : TestTag {
    object PlusButton : AddSubRowTag()
    object MinusButton : AddSubRowTag()
}

data class SelectItemHeaderTag(val name: String) : TestTag {
    override val tagName: String
        get() = super.tagName + ".$name"
}