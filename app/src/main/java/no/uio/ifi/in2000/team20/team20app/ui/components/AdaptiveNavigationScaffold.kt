package no.uio.ifi.in2000.team20.team20app.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import no.uio.ifi.in2000.team20.team20app.util.Screen
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team20.team20app.util.Constants.DEFAULT_PADDING_DP
import no.uio.ifi.in2000.team20.team20app.util.Constants.LARGE_PADDING_DP
import no.uio.ifi.in2000.team20.team20app.util.LocalWindowSizeClass

/*
Main changes done (24.04.2026 adaptive-navigation-impl):
- Reduced repeating code by iterating through the Screen entries instead, using onNavigate as a general function.
- Added topbar inside a nested scaffold, but still unsure if this is best practice.
*/
@Composable
fun AdaptiveNavigationScaffold (
    title: String,
    highlightedDest: NavKey?,
    onNavigate: (Screen) -> Unit,
    onOpenSettings: () -> Unit = {},
    onBackClick: () -> Unit = {},
    content: @Composable (Modifier) -> Unit
){

    val compactScreenWidth = !LocalWindowSizeClass.current
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            Screen.entries.forEach { screen ->
                item(
                    selected = screen.route == highlightedDest,
                    onClick = { onNavigate(screen) },
                    icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                    label = { screen.title },
                )
            }
        },

        layoutType = if (!compactScreenWidth) {
            NavigationSuiteType.NavigationRail
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MaterialTheme.colorScheme.surface,
            navigationBarContentColor = MaterialTheme.colorScheme.onSurface,
            navigationRailContainerColor = MaterialTheme.colorScheme.surface,
        ),
    ){
        content(Modifier.padding(0.dp))
    }
}