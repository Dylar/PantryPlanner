package de.bitb.pantryplaner.usecase.item

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.capitalizeFirstCharacter
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.SettingsRepository
import kotlinx.coroutines.flow.first

class EditItemCategoryUC(
    private val settingsRepo: SettingsRepository,
    private val itemRepo: ItemRepository,
) {
    suspend operator fun invoke(
        oldCategory: String,
        newCategory: String,
        color: Color,
    ): Result<Unit> {
        return tryIt {
            val itemsResp = itemRepo.getUserItems().first()
            if (itemsResp is Result.Error) return@tryIt itemsResp.castTo()

            val settingsResp = settingsRepo.getSettings().first()
            if (settingsResp is Result.Error) return@tryIt settingsResp.castTo()

            val newCat = newCategory.capitalizeFirstCharacter()
            val oldCat = oldCategory.capitalizeFirstCharacter()
            val oldSettings = settingsResp.data!!
            val categoryColors = oldSettings.categoryColors.toMutableMap().apply {
                remove(oldCat)
                this[newCat] = color.toArgb()
            }
            val settings = oldSettings.copy(categoryColors = categoryColors)
            val items = itemsResp.data!!
                .toMutableList()
                .apply {
                    replaceAll {
                        if (it.creator != oldSettings.uuid || it.category != oldCategory) it
                        else it.copy(category = newCat)
                    }
                }

            if (items.isNotEmpty()) {
                val saveResp = itemRepo.saveItems(items)
                if (saveResp is Result.Error) return@tryIt saveResp
            }

            settingsRepo.saveSettings(settings)
        }
    }
}