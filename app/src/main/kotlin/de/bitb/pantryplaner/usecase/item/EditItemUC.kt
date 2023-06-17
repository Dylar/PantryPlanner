package de.bitb.pantryplaner.usecase.item

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.model.Item

class EditItemUC(
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(
        item: Item,
        name: String,
        category: String,
        color: Color,
    ): Resource<Unit> {
        return tryIt {
            itemRepo.saveItems(
                listOf(
                    item.copy(
                        name = name,
                        category = category,
                        colorHex = color.toArgb()
                    )
                )
            )
        }
    }
}