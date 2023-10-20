package de.bitb.pantryplaner.ui.base.testTags

sealed class ConfirmDialogTag : TestTag {
    object ConfirmButton : ConfirmDialogTag()
    object DismissButton : ConfirmDialogTag()
}

sealed class AddEditStockDialogTag : TestTag {
    object DialogTag : AddEditStockDialogTag()
    object NameLabel : AddEditStockDialogTag()
    object ConfirmButton : AddEditStockDialogTag()
}

sealed class AddEditItemDialogTag : TestTag {
    object DialogTag : AddEditItemDialogTag()
    object NameLabel : AddEditItemDialogTag()
    object ConfirmButton : AddEditItemDialogTag()
}

sealed class AddEditChecklistDialogTag : TestTag {
    object DialogTag : AddEditChecklistDialogTag()
    object NameLabel : AddEditChecklistDialogTag()
    object ConfirmButton : AddEditChecklistDialogTag()
}