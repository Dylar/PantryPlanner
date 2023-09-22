package de.bitb.pantryplaner.ui.base.testTags

sealed class ProfilePageTag : TestTag {
    object AppBar : ProfilePageTag()
    object SettingsButton : ProfilePageTag()
    object QRInfo : ProfilePageTag()
    object QRLabel : ProfilePageTag()
    object NewLocationButton : ProfilePageTag()
    object ScanButton : ProfilePageTag()
    data class LocationItem(val name: String) : ProfilePageTag() {
        override val tagName: String
            get() = super.tagName + ".$name"
    }
}

sealed class AddEditLocationDialogTag : TestTag {
    object NameLabel : AddEditLocationDialogTag()
    object ConfirmButton : AddEditLocationDialogTag()
}