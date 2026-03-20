package no.uio.ifi.in2000.team20.team20app.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomTheme (
    val background: Color,
    val backgroundGradient: Color,
    val middleground: Color,
    val foreground: Color,
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

val lightThemeColors = CustomTheme (
    background = Platinum,
    backgroundGradient = PaleSky,
    middleground = BrightWhite, // Should be 50% opaque
    foreground = BrightWhite,
    detail = CloudySky,
    button = MayaBlue,
    buttonDetail = DustyBlue,
    disabledButton = AliceBlue,
    disabledButtonDetail = CloudySky,
    error = Salmon,
    warning = RoyalGold,
    isLight = true,
)

val darkThemeColors = CustomTheme( // To be changed
    background = Midnight,
    backgroundGradient = Color.Black,
    middleground = Charcoal,
    foreground = DarkLake,
    detail = Color.Black,
    button = MayaBlue,
    buttonDetail = DustyBlue,
    disabledButton = AliceBlue,
    disabledButtonDetail = Color.Black,
    error = Salmon,
    warning = RoyalGold,
    isLight = false
)

val LocalTheme = staticCompositionLocalOf<CustomTheme> {
    error("Color not provided")
}