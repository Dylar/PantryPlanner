package de.bitb.pantryplaner.ui.base.testTags

sealed class ConfirmDialogTag : TestTag {
    object ConfirmButton : ConfirmDialogTag()
    object DismissButton : ConfirmDialogTag()
}

sealed class NewAppVersionDialogTag : TestTag {
    object DialogTag : NewAppVersionDialogTag()
    object ConfirmButton : NewAppVersionDialogTag()
    object CancelButton : NewAppVersionDialogTag()
}

sealed class AddUserDialogTag : TestTag {
    object DialogTag : AddUserDialogTag()
    object EmailLabel : AddUserDialogTag()
    object ScanButton : AddUserDialogTag()
    object ConfirmButton : AddUserDialogTag()
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
    object CancelButton : AddEditItemDialogTag()
}

sealed class AddEditChecklistDialogTag : TestTag {
    object DialogTag : AddEditChecklistDialogTag()
    object NameLabel : AddEditChecklistDialogTag()
    object ConfirmButton : AddEditChecklistDialogTag()
}

sealed class EditCategoryDialogTag : TestTag {
    object DialogTag : EditCategoryDialogTag()
    object CategoryLabel : EditCategoryDialogTag()
    object SaveButton : EditCategoryDialogTag()
}

sealed class SelectChecklistDialogTag : TestTag {
    object DialogTag : SelectChecklistDialogTag()
    object NewButton : SelectChecklistDialogTag()
}

sealed class SelectStockDialogTag : TestTag {
    object DialogTag : SelectStockDialogTag()
    object NewButton : SelectStockDialogTag()
}
