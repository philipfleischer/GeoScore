package no.uio.ifi.in2000.team20.team20app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
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
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team20.team20app.ui.theme.LocalTheme
import no.uio.ifi.in2000.team20.team20app.util.LocalWindowSizeClass
import no.uio.ifi.in2000.team20.team20app.util.Constants.DEFAULT_PADDING_DP
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize

@Composable
fun AdaptiveNavigationScaffold (
    highlightedDest: NavKey?,
    onNavigate: (Screen) -> Unit,
    surroundColor: Color = MaterialTheme.colorScheme.surface,
    floatingNav: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
){
    val compactScreenWidth = !LocalWindowSizeClass.current
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    val layoutType =
        if (!compactScreenWidth) { NavigationSuiteType.NavigationRail }
        else { NavigationSuiteType.NavigationBar }

    if (floatingNav) {
        var navSize by remember {mutableStateOf(IntSize.Zero)}
        val density = LocalDensity.current
        val contentInsets = with(density) {
            if (compactScreenWidth) PaddingValues(bottom = navSize.height.toDp())
            else PaddingValues(start = (navSize.width).toDp())
        }

        Box(Modifier.fillMaxSize()) {
            content(contentInsets)
            NavSuiteContent(
                layoutType = layoutType,
                highlightedDest = highlightedDest,
                onNavigate = onNavigate,
                borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(
                    if(compactScreenWidth) Alignment.BottomCenter
                    else Alignment.CenterStart
                    )
                    .onGloballyPositioned { navSize = it.size }
                ,
            )
        }
    } else {
        NavigationSuiteScaffoldLayout(
            navigationSuite = {
                Box(Modifier.background(surroundColor)) {
                    NavSuiteContent(
                        layoutType = layoutType,
                        highlightedDest = highlightedDest,
                        onNavigate = onNavigate,
                    )
                }
            },
            layoutType = layoutType,
        ) {
            content(PaddingValues(0.dp))
        }
    }
}

@Composable
private fun NavSuiteContent(
    modifier: Modifier = Modifier,
    layoutType: NavigationSuiteType,
    highlightedDest: NavKey?,
    onNavigate: (Screen) -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = Color.Transparent
){
    val theme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding((DEFAULT_PADDING_DP*2).dp)
        ,
        shape = RoundedCornerShape(100),
        color = containerColor,
        border = BorderStroke(0.3.dp, borderColor)
    ){
        val itemColors = NavigationSuiteDefaults.itemColors(
            navigationBarItemColors = NavigationBarItemDefaults.colors(
                selectedIconColor = theme.onPrimary,
                indicatorColor = theme.primary
            ),
            navigationRailItemColors = NavigationRailItemDefaults.colors(
                selectedIconColor = theme.onPrimary,
                indicatorColor = theme.primary
            )
        )
        NavigationSuite(
            layoutType = layoutType,
            colors = NavigationSuiteDefaults.colors(
                navigationBarContainerColor = theme.surfaceContainerLow,
                navigationRailContainerColor = theme.surfaceContainerLow,

            ),
        ){
            Screen.entries.forEach { screen ->
                item(
                    selected = screen.route == highlightedDest,
                    onClick = { onNavigate(screen) },
                    icon = { Icon(screen.icon, screen.title)},
                    label = { Text(screen.title, color = theme.onSurfaceVariant) },
                    colors = itemColors,
                )
            }
        }
    }
}