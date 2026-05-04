package no.uio.ifi.in2000.team20.team20app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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
    val trafficGreen: Color,
    val trafficYellow: Color,
    val trafficRed: Color,

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
    trafficGreen = TrafficGreen,
    trafficYellow = TrafficYellow,
    trafficRed = TrafficRed,
    isLight = true,
)

//TODO: Change dark theme colors
val darkThemeColors = CustomTheme(
    background = Midnight,
//    backgroundGradient = Color.Black,
    tertiary = PaleSky,
    secondary = Charcoal,
    primary = DarkLake,
    detail = Midnight,
    button = MayaBlue,
    buttonDetail = DustyBlue,
    disabledButton = AliceBlue,
    disabledButtonDetail = Charcoal,
    error = Salmon,
    warning = RoyalGold,
    trafficGreen = TrafficGreen,
    trafficYellow = TrafficYellow,
    trafficRed = TrafficRed,
    isLight = false
)

// Material3 Color Schemes
val LightColorScheme = lightColorScheme(
    primary = DarkBlue,
    onPrimary = BrightWhite,
    primaryContainer = PaleSky,
    onPrimaryContainer = DarkBlue,
    secondary = MayaBlue,
    onSecondary = BrightWhite,
    secondaryContainer = AliceBlue,
    onSecondaryContainer = DarkBlue,
    background = Platinum,
    onBackground = DarkBlue,
    surface = BrightWhite,
    onSurface = DarkBlue,
    surfaceVariant = PaleSky,
    onSurfaceVariant = SlateGray,
    error = Salmon,
    onError = BrightWhite,
)

val DarkColorScheme = darkColorScheme(
    primary = MayaBlue,
    onPrimary = Midnight,
    primaryContainer = DarkLake,
    onPrimaryContainer = MayaBlue,
    secondary = MayaBlue,
    onSecondary = Midnight,
    background = Midnight,
    onBackground = BrightWhite,
    surface = Charcoal,
    onSurface = BrightWhite,
    surfaceVariant = DarkLake,
    onSurfaceVariant = SteelGray,
    error = Salmon,
    onError = Midnight,
)

val LocalTheme = staticCompositionLocalOf<CustomTheme> {
    error("Color not provided")
}