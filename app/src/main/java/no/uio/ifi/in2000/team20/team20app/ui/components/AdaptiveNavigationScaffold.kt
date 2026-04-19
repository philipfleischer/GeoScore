package no.uio.ifi.in2000.team20.team20app.ui.components

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import no.uio.ifi.in2000.team20.team20app.ui.navigation.Route
import no.uio.ifi.in2000.team20.team20app.util.Screen

/*
I didn't change much of the original logic for the navigation,
but I noticed a lot of repetition. I'm sure onSomeScreenClick can be simplified
to only one onScreenClick. I wrote a Screen enum class, so maybe this can be utilized
*/
@Composable
fun AdaptiveNavigationScaffold (
    currentDestination: NavKey?,
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    content: @Composable () -> Unit
){
    /*
    windowWidthClass calculates the current available screen size and
    returns true if the width is equal to or bigger than a medium window size class.
    If windowWidthClass returns false, it is typically a mobile phone in portrait or multiple windows mode.
    */
    val windowWidthClass = currentWindowAdaptiveInfo()
        .windowSizeClass
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            // TODO: Simplify and remove duplication of code
            item(
                selected = currentDestination is Route.HomeDestination,
                onClick = onHomeClick,
                icon = { Screen.HOME.icon },
                label = { Screen.HOME.title }
            )
            item(
                selected = currentDestination is Route.MapDestination,
                onClick = onMapClick,
                icon = { Screen.MAP.icon },
                label = { Screen.MAP.title }
            )
            item(
                selected = currentDestination is Route.FavoritesDestination,
                onClick = onFavoritesClick,
                icon = { Screen.FAVORITES.icon },
                label = { Screen.FAVORITES.title }
            )
        },

        layoutType = if (windowWidthClass) {
            NavigationSuiteType.NavigationRail
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
        }
    ){
        content()
    }
}