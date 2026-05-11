package no.uio.ifi.in2000.team20.team20app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import no.uio.ifi.in2000.team20.team20app.util.Screen
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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

    val navInsets = if (compactScreenWidth) WindowInsets.navigationBars
    else WindowInsets.systemBars.only(WindowInsetsSides.Start)

    if (floatingNav) {
        var navSize by remember {mutableStateOf(IntSize.Zero)}
        val density = LocalDensity.current
        val contentInsets = with(density) {
            if (compactScreenWidth) PaddingValues(bottom = navSize.height.toDp())
            else PaddingValues(start = (navSize.width).toDp())
        }

        Box(Modifier.windowInsetsPadding(navInsets)) {
            content(contentInsets)
            NavSuiteContent(
                layoutType = layoutType,
                highlightedDest = highlightedDest,
                onNavigate = onNavigate,
                compact = compactScreenWidth,
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
                Box(Modifier.background(surroundColor).windowInsetsPadding(navInsets)) {
                    NavSuiteContent(
                        layoutType = layoutType,
                        highlightedDest = highlightedDest,
                        onNavigate = onNavigate,
                        compact = compactScreenWidth
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
    compact: Boolean = true
){
    val theme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier
            .padding(
                top = if (compact) DEFAULT_PADDING_DP.dp else (DEFAULT_PADDING_DP*2).dp,
                start = (DEFAULT_PADDING_DP*2).dp,
                end = if (compact) (DEFAULT_PADDING_DP*2).dp else DEFAULT_PADDING_DP.dp,
                bottom = DEFAULT_PADDING_DP.dp
            )
        ,
        shape = RoundedCornerShape(100),
        color = containerColor,
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