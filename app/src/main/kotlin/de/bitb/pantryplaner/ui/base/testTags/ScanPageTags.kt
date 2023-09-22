package de.bitb.pantryplaner.ui.base.testTags

sealed class ScanPageTag : TestTag {
    object AppBar : ScanPageTag()
    object ScanLabel : ScanPageTag()
}
