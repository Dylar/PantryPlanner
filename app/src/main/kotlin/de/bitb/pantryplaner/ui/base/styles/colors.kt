package de.bitb.pantryplaner.ui.base.styles

import androidx.compose.ui.graphics.Color
val Blue10 = Color(0xff001433)
val Blue20 = Color(0xff002866)
val Blue30 = Color(0xff003c99)
val Blue40 = Color(0xff0052cc)
val Blue80 = Color(0xff99ccff)
val Blue90 = Color(0xffcce6ff)

val DarkBlue10 = Color(0xff0d1a33)
val DarkBlue20 = Color(0xff193366)
val DarkBlue30 = Color(0xff264d99)
val DarkBlue40 = Color(0xff3366cc)
val DarkBlue80 = Color(0xffb3ccff)
val DarkBlue90 = Color(0xffd9e6ff)

val Green10 = Color(0xff003314)
val Green20 = Color(0xff006627)
val Green30 = Color(0xff00993b)
val Green40 = Color(0xff00cc4e)
val Green80 = Color(0xff99ffc0)
val Green90 = Color(0xffccffe0)

val DarkGreen10 = Color(0xff0d260d)
val DarkGreen20 = Color(0xff194d19)
val DarkGreen30 = Color(0xff267326)
val DarkGreen40 = Color(0xff339933)
val DarkGreen80 = Color(0xffb3e6b3)
val DarkGreen90 = Color(0xffd9f2d9)

val Violet10 = Color(0xff330033)
val Violet20 = Color(0xff660066)
val Violet30 = Color(0xff990099)
val Violet40 = Color(0xffcc00cc)
val Violet80 = Color(0xffff99ff)
val Violet90 = Color(0xffffccff)

val Red10 = Color(0xFF410001)
val Red20 = Color(0xFF680003)
val Red30 = Color(0xFF930006)
val Red40 = Color(0xFFBA1B1B)
val Red80 = Color(0xFFFFB4A9)
val Red90 = Color(0xFFFFDAD4)

val Grey10 = Color(0xFF191C1D)
val Grey20 = Color(0xFF2D3132)
val Grey90 = Color(0xFFE0E3E3)
val Grey95 = Color(0xFFEFF1F1)
val Grey99 = Color(0xFFFBFDFD)

val GreenGrey30 = Color(0xFF316847)
val GreenGrey50 = Color(0xFF52ad76)
val GreenGrey60 = Color(0xFF74be92)
val GreenGrey80 = Color(0xFFbadec8)
val GreenGrey90 = Color(0xFFdcefe4)

object BaseColors {
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)

    val LightGray = Color(0xFFE0E0E0)
    val DarkGray = Color(0xFF202020)

    val AdultBlue = Color(0xff0B5394)
    val LightGreen = Color(0xffe7ed9b)
    val DarkGreen = Color(0xFF006400)
    val BabyBlue = Color(0xff89CFF0)
    val SunYellow = Color(0xFFFFFF00)
    val ZergPurple = Color(0xffA259FF)
    val FireRed = Color(0xFFFF4500)

    val UnselectedColor = Black
    val SelectableColors = listOf(
        LightGray,
        FireRed,
        ZergPurple,
        BabyBlue,
        AdultBlue,
        LightGreen,
        DarkGreen,
    )
    val FilterColors = listOf(UnselectedColor, *SelectableColors.toTypedArray())
}
