package de.bitb.pantryplaner.ui.base.styles

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@Composable
fun PantryAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
//    val useDynamicColors = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colors = when {
//        useDynamicColors && useDarkTheme -> dynamicDarkColorScheme(LocalContext.current)
//        useDynamicColors && !useDarkTheme -> dynamicLightColorScheme(LocalContext.current)
        useDarkTheme -> DarkColorPalette
        else -> LightColorPalette
    }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

private val DarkColorPalette = darkColors(
    primary = Blue80,
    onPrimary = Blue20,
//    primaryContainer = Blue30,
//    onPrimaryContainer = Blue90,
//    inversePrimary = Blue40,
    secondary = DarkBlue80,
    onSecondary = DarkBlue20,
//    secondaryContainer = DarkBlue30,
//    onSecondaryContainer = DarkBlue90,
//    tertiary = Violet80,
//    onTertiary = Violet20,
//    tertiaryContainer = Violet30,
//    onTertiaryContainer = Violet90,
    error = Red80,
    onError = Red20,
//    errorContainer = Red30,
//    onErrorContainer = Red90,
    background = Grey10,
    onBackground = Grey90,
    surface = Blue10,
    onSurface = Blue80,
//    inverseSurface = Grey90,
//    inverseOnSurface = Grey10,
//    surfaceVariant = Blue10,
//    onSurfaceVariant = Blue80,
//    outline = Blue80
)

private val LightColorPalette = lightColors(
    primary = Blue40,
    onPrimary = BaseColors.White,
//    primaryContainer = Blue90,
//    onPrimaryContainer = Blue10,
//    inversePrimary = Blue80,
    secondary = DarkBlue40,
    onSecondary = BaseColors.White,
//    secondaryContainer = DarkBlue90,
//    onSecondaryContainer = DarkBlue10,
//    tertiary = Violet40,
//    onTertiary = BaseColors.White,
//    tertiaryContainer = Violet90,
//    onTertiaryContainer = Violet10,
    error = Red40,
    onError = BaseColors.White,
//    errorContainer = Red90,
//    onErrorContainer = Red10,
    background = Grey99,
    onBackground = Grey10,
    surface = Blue90,
    onSurface = Blue10,
//    inverseSurface = Grey20,
//    inverseOnSurface = Grey95,
//    surfaceVariant = Blue90,
//    onSurfaceVariant = Blue10,
//    outline = Blue40
)

//private val DarkColorPalette = darkColorScheme(
//    primary = Green80,
//    onPrimary = Green20,
//    primaryContainer = Green30,
//    onPrimaryContainer = Green90,
//    inversePrimary = Green40,
//    secondary = DarkGreen80,
//    onSecondary = DarkGreen20,
//    secondaryContainer = DarkGreen30,
//    onSecondaryContainer = DarkGreen90,
//    tertiary = Violet80,
//    onTertiary = Violet20,
//    tertiaryContainer = Violet30,
//    onTertiaryContainer = Violet90,
//    error = Red80,
//    onError = Red20,
//    errorContainer = Red30,
//    onErrorContainer = Red90,
//    background = Grey10,
//    onBackground = Grey90,
//    surface = GreenGrey30,
//    onSurface = GreenGrey80,
//    inverseSurface = Grey90,
//    inverseOnSurface = Grey10,
//    surfaceVariant = GreenGrey30,
//    onSurfaceVariant = GreenGrey80,
//    outline = GreenGrey80
//)
//
//private val LightColorPalette = lightColorScheme(
//    primary = Green40,
//    onPrimary = Color.White,
//    primaryContainer = Green90,
//    onPrimaryContainer = Green10,
//    inversePrimary = Green80,
//    secondary = DarkGreen40,
//    onSecondary = Color.White,
//    secondaryContainer = DarkGreen90,
//    onSecondaryContainer = DarkGreen10,
//    tertiary = Violet40,
//    onTertiary = Color.White,
//    tertiaryContainer = Violet90,
//    onTertiaryContainer = Violet10,
//    error = Red40,
//    onError = Color.White,
//    errorContainer = Red90,
//    onErrorContainer = Red10,
//    background = Grey99,
//    onBackground = Grey10,
//    surface = GreenGrey90,
//    onSurface = GreenGrey30,
//    inverseSurface = Grey20,
//    inverseOnSurface = Grey95,
//    surfaceVariant = GreenGrey90,
//    onSurfaceVariant = GreenGrey30,
//    outline = GreenGrey50
//)
