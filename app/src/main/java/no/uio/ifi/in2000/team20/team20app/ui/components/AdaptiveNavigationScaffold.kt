package no.uio.ifi.in2000.team20.team20app.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import no.uio.ifi.in2000.team20.team20app.ui.navigation.Route
import no.uio.ifi.in2000.team20.team20app.util.Screen
import androidx.compose.foundation.layout.padding

/*
Main changes done (24.04.2026 adaptive-navigation-impl):
- Reduced repeating code by iterating through the Screen entries instead, using onNavigate as a general function.
- Added topbar inside a nested scaffold, but still unsure if this is best practice.
*/
@Composable
fun AdaptiveNavigationScaffold (
    title: String,
    currentDestination: NavKey?,
    onNavigate: (Screen) -> Unit,
    onOpenSettings: () -> Unit,
    content: @Composable (Modifier) -> Unit
){

    val windowWidthClass = currentWindowAdaptiveInfo()
        .windowSizeClass
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            Screen.entries.forEach { screen ->
                item(
                    selected = screen.route == currentDestination,
                    onClick = { onNavigate(screen) },
                    icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                    label = { screen.title },
                )
            }
        },

        layoutType = if (windowWidthClass) {
            NavigationSuiteType.NavigationRail
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
        }
    ){
        Scaffold(
            topBar = { SharedTopAppBar(
                title = title,
                onOpenSettings = onOpenSettings)
            }
        ) { innerPadding ->
            content(Modifier.padding(innerPadding))
        }
    }
}