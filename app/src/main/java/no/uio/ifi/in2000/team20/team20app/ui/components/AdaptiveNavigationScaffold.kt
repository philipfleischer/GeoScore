package no.uio.ifi.in2000.team20.team20app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import no.uio.ifi.in2000.team20.team20app.util.Screen
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team20.team20app.ui.theme.LocalTheme
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
    val theme = LocalTheme.current
    val compactScreenWidth = !LocalWindowSizeClass.current
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    val navRail = NavigationSuiteType.NavigationRail
    val navBar = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())

    val layoutType = if (!compactScreenWidth) { navRail } else { navBar }
    val cornerPercentage = 100
    val navShape = RoundedCornerShape( cornerPercentage )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = theme.background
    ){
        NavigationSuiteScaffoldLayout(
            navigationSuite = {
                Surface(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = navShape,
                    color = theme.background
                ){
                    NavigationSuite(
                        layoutType = layoutType,
                        colors = NavigationSuiteDefaults.colors(
                            navigationBarContainerColor = theme.primary,
                            navigationBarContentColor = theme.primary,
                            navigationRailContainerColor = theme.primary,
                            navigationRailContentColor = theme.primary
                        )
                    ) {
                        Screen.entries.forEach { screen ->
                            item(
                                selected = screen.route == highlightedDest,
                                onClick = { onNavigate(screen) },
                                icon = { Icon(screen.icon, contentDescription = screen.title) },
                                label = { Text(screen.title) },
                            )
                        }
                    }
                }
            },
            layoutType = layoutType,
        ) {
            content(Modifier.padding(0.dp))
        }
    }
}