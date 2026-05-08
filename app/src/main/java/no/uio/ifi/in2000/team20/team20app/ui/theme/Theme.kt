package no.uio.ifi.in2000.team20.team20app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomTheme (
    val background: Color,  // Background
    val tertiary: Color,    // Headers
    val secondary: Color,   // Topbar, navbar/rail
    val primary: Color,     // Cards, boxes and columns
    val detail: Color,      // Borders, decoration
    val button: Color,
    val selected: Color,    // Selected elements such as navigation icons
//    val buttonDetail: Color,
//    val disabledButton: Color,
//    val disabledButtonDetail: Color,
    val error: Color,
    val warning: Color,
    val trafficGreen: Color,
    val trafficYellow: Color,
    val trafficRed: Color,

    val onBackground: Color,    // Text and contrast on background
    val onPrimary: Color,       // Text on cards, boxes and columns
    val onSecondary: Color,     // Text and icons on topbar and navigation bar/rail
//    val onTertiary: Color,
    val onSelected: Color,      // Icon colors when selected
//    val onButton: Color,
//    val onDisabledButton: Color,
//    val onError: Color,
//    val onWarning: Color,

    val isLight: Boolean,
    )

//TODO: Change light theme colors
val lightThemeColors = CustomTheme(
    background = PaleSky,
    onBackground = DarkLake,
    tertiary = DarkBlue,
//    onTertiary = Platinum,
    secondary = MayaBlue,
    onSecondary = Midnight,
    primary = Platinum, // Cards, boxes, columns
    onPrimary = Midnight, // Main text color on card, boxes, columns
    detail = CloudySky,
    button = MayaBlue,
    selected = MayaBlue,
    onSelected = DarkBlue,
//    buttonDetail = DustyBlue,
//    disabledButton = AliceBlue,
//    disabledButtonDetail = CloudySky,
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
    onBackground = Platinum,
    tertiary = PaleSky,
//    onTertiary = ,
    secondary = Charcoal,
    primary = DarkLake,
    detail = Midnight,
    button = MayaBlue,
//    buttonDetail = DustyBlue,
//    disabledButton = AliceBlue,
//    disabledButtonDetail = Charcoal,
    selected = DarkLake,
    onPrimary = Platinum,
    onSecondary = Platinum,
    onSelected = Platinum,
    error = Salmon,
    warning = RoyalGold,
    trafficGreen = TrafficGreen,
    trafficYellow = TrafficYellow,
    trafficRed = TrafficRed,
    isLight = false,
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