package no.uio.ifi.in2000.team20.team20app.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomTheme (
    val background: Color,
//    val backgroundGradient: Color,
    val tertiary: Color, // For headers and navigation bar
    val secondary: Color, // middleground
    val primary: Color, // foreground
    val detail: Color,
    val button: Color,
    val buttonDetail: Color,
    val disabledButton: Color,
    val disabledButtonDetail: Color,
    val error: Color,
    val warning: Color,

//    val onBackground: Color,
//    val onMiddleground: Color,
//    val onForeground: Color,
//    val onButton: Color,
//    val onDisabledButton: Color,
//    val onError: Color,
//    val onWarning: Color,

    val isLight: Boolean,
    )

//TODO: Change light theme colors
val lightThemeColors = CustomTheme(
    background = Platinum,
    tertiary = DarkBlue,
//    backgroundGradient = PaleSky,
    secondary = BrightWhite, // Should be 50% opaque
    primary = BrightWhite,
    detail = CloudySky,
    button = MayaBlue,
    buttonDetail = DustyBlue,
    disabledButton = AliceBlue,
    disabledButtonDetail = CloudySky,
    error = Salmon,
    warning = RoyalGold,
    isLight = true,
)

//TODO: Change dark theme colors
val darkThemeColors = CustomTheme(
    background = Midnight,
//    backgroundGradient = Color.Black,
    tertiary = Midnight,
    secondary = Charcoal,
    primary = DarkLake,
    detail = Midnight,
    button = MayaBlue,
    buttonDetail = DustyBlue,
    disabledButton = AliceBlue,
    disabledButtonDetail = Charcoal,
    error = Salmon,
    warning = RoyalGold,
    isLight = false
)

val LocalTheme = staticCompositionLocalOf<CustomTheme> {
    error("Color not provided")
}