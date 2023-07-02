package de.bitb.pantryplaner.ui.base.styles

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun PantryAppTheme(useDarkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme( // TODO make theme
//        colors = if (useDarkTheme) darkColorPalette else lightColorPalette,
        colors = darkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}