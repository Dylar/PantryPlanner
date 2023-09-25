package de.bitb.pantryplaner.ui.base.testTags

sealed class ConfirmDialogTag : TestTag {
    object ConfirmButton : ConfirmDialogTag()
    object DismissButton : ConfirmDialogTag()
}

sealed class AddEditLocationDialogTag : TestTag {
    object NameLabel : AddEditLocationDialogTag()
    object ConfirmButton : AddEditLocationDialogTag()
}