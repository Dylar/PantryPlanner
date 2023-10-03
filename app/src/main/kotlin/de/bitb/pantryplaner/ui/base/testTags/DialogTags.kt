package de.bitb.pantryplaner.ui.base.testTags

sealed class ConfirmDialogTag : TestTag {
    object ConfirmButton : ConfirmDialogTag()
    object DismissButton : ConfirmDialogTag()
}

sealed class AddEditStockDialogTag : TestTag {
    object NameLabel : AddEditStockDialogTag()
    object ConfirmButton : AddEditStockDialogTag()
}

sealed class AddEditItemDialogTag : TestTag {
    object NameLabel : AddEditItemDialogTag()
    object ConfirmButton : AddEditItemDialogTag()
}

sealed class AddEditChecklistDialogTag : TestTag {
    object NameLabel : AddEditChecklistDialogTag()
    object ConfirmButton : AddEditChecklistDialogTag()
}