package de.bitb.pantryplaner.ui.base.styles

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun PantryAppTheme(useDarkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (useDarkTheme) darkColorPalette else lightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}