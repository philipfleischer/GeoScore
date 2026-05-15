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
    val error: Color,
    val warning: Color,
    val trafficGreen: Color,
    val trafficYellow: Color,
    val trafficRed: Color,

    val onBackground: Color,    // Text and contrast on background
    val onPrimary: Color,       // Text on cards, boxes and columns
    val onSecondary: Color,     // Text and icons on topbar and navigation bar/rail
    val onSelected: Color,      // Icon colors when selected

    val isLight: Boolean,
    )

val lightThemeColors = CustomTheme(
    background = LightBlue,
    onBackground = DarkGray,
    tertiary = DarkBlue,
    secondary = MayaBlue,
    onSecondary = Black,
    primary = OffWhite, // Cards, boxes, columns
    onPrimary = Black, // Main text color on card, boxes, columns
    detail = CloudySky,
    button = MayaBlue,
    selected = MayaBlue,
    onSelected = DarkBlue,
    error = Salmon,
    warning = RoyalGold,
    trafficGreen = TrafficGreen,
    trafficYellow = TrafficYellow,
    trafficRed = TrafficRed,
    isLight = true,
)

val darkThemeColors = CustomTheme(
    background = Black,
    onBackground = OffWhite,
    tertiary = LightBlue,
    secondary = Charcoal,
    primary = DarkGray,
    detail = Black,
    button = MayaBlue,
    selected = DarkGray,
    onPrimary = OffWhite,
    onSecondary = OffWhite,
    onSelected = OffWhite,
    error = Salmon,
    warning = RoyalGold,
    trafficGreen = TrafficGreen,
    trafficYellow = TrafficYellow,
    trafficRed = TrafficRed,
    isLight = false,
)

// Material3 Color Schemes
val LightColorScheme = lightColorScheme(
    surface = LightBlue, // Essentially background
    onSurface = Charcoal, // Text on background
    surfaceContainerHigh = White, // Cards
    surfaceContainerLow = OffWhite, // Bars/rails
    onSurfaceVariant = Charcoal, // Text/ icons on bars/rails and cards
    primary = MayaBlue, // Buttons/selected
    onPrimary = Charcoal,
    secondary = DarkBlue,
    onSecondary = White,
    outline = SlateGray,
    error = Salmon,
)

val DarkColorScheme = darkColorScheme(
    surface = Midnight, // Essentially background
    onSurface = White, // Text on background
    surfaceContainerHigh = DarkGray, // Cards
    surfaceContainerLow = DarkGray, // Bars/rails
    onSurfaceVariant = White, //
    primary = MayaBlue,
    onPrimary = Black,
    secondary = MayaBlue,
    onSecondary = Black,
    outline = Color.Transparent,
    error = Salmon
)

val LocalTheme = staticCompositionLocalOf<CustomTheme> {
    error("Color not provided")
}