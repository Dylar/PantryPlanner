package de.bitb.pantryplaner.usecase.item

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.model.Item

class AddItemUC(
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(name: String, category: String, color: Color): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                if (name.isBlank()) {
                    return@tryIt "Name darf nicht leer sein".asResourceError()
                }
                val item = Item(name, category, colorHex = color.toArgb())
                itemRepo.addItem(item)
            },
        )
    }
}