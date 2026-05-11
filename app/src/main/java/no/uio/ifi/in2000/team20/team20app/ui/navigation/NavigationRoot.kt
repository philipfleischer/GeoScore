package no.uio.ifi.in2000.team20.team20app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import no.uio.ifi.in2000.team20.team20app.ui.components.AdaptiveNavigationScaffold
import no.uio.ifi.in2000.team20.team20app.ui.screens.result.ClimateStatsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.result.GeoscoreScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.saved.SavedScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.saved.SavedViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.map.MapScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.map.MapViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.result.GeoScoreViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel
import no.uio.ifi.in2000.team20.team20app.util.Screen

@Composable
fun NavigationRoot() {
    // Shared ViewModels, activity-scoped, single instance across all nav entries
    val appViewModel: AppViewModel = hiltViewModel()
    val frostViewModel: FrostViewModel = hiltViewModel()
    // SavedViewModel is shared so that isCurrentSaved stays consistent across all screens
    val savedViewModel: SavedViewModel = hiltViewModel()

    //BackStack
    val backStack = rememberNavBackStack(Screen.HOME.route)

    //Navigation-lambdas
    val goBack: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    val onNavigate: (Screen) -> Unit = {
        screen -> backStack.add(screen.route as Route)
    }

    val goToSettings: () -> Unit = { backStack.add(Route.SettingsDestination) }
    val goToSearch: () -> Unit = { backStack.add(Route.SearchDestination) }

    // Helper to pop back to (but not including) a destination matching the predicate
    val popBackTo: ((NavKey) -> Boolean) -> Boolean = { predicate ->
        val targetIndex = backStack.indexOfLast { predicate(it) }
        if (targetIndex >= 0) {
            while (backStack.size > targetIndex + 1) {
                backStack.removeLastOrNull()
            }
            true
        } else {
            false
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = goBack,
        entryProvider = entryProvider {
            entry<Route.HomeDestination> {
                // Screen-specific ViewModel scoped to this nav entry
                val homeViewModel: HomeViewModel = hiltViewModel()

                AdaptiveNavigationScaffold(
//                    title = Screen.HOME.title,
                    onNavigate = onNavigate,
//                    onOpenSettings = goToSettings,
                    highlightedDest = Screen.HOME.route,
                ) { insets ->
                    HomeScreen(
                        onOpenSearch = goToSearch,
                        modifier = Modifier.padding(insets),
                        viewModel = homeViewModel,
                        sharedViewModel = appViewModel,
                        savedViewModel = savedViewModel,
                        frostViewModel = frostViewModel
                    )
                }
            }

            entry<Route.MapDestination> {
                // Screen-specific ViewModel scoped to this nav entry
                val mapViewModel: MapViewModel = hiltViewModel()

                AdaptiveNavigationScaffold(
//                    title = Screen.MAP.title,
                    onNavigate = onNavigate,
//                    onOpenSettings = goToSettings,
                    highlightedDest = Screen.MAP.route,
                    floatingNav = true
                ) { insets ->
                    MapScreen(
                        modifier = Modifier.padding(insets),
                        sharedViewModel = appViewModel,
                        savedViewModel = savedViewModel,
                        mapViewModel = mapViewModel,
                        onOpenSearch = goToSearch,
                        onOpenReport = {
                            backStack.add(Route.GeoscoreDestination(appViewModel.selectedLocation.value!!))
                        }
                    )
                }
            }

            entry<Route.SavedDestination> {
                AdaptiveNavigationScaffold(
//                    title = Screen.SAVED.title,
                    onNavigate = onNavigate,
//                    onOpenSettings = goToSettings,
                    highlightedDest = Screen.SAVED.route
                ) { insets ->
                    SavedScreen(
                        modifier = Modifier.padding(insets),
                        sharedViewModel = appViewModel,
                        savedViewModel = savedViewModel,
                        onSavedClick = { location ->
                            appViewModel.setSelectedArea(location)
                            backStack.add(Route.GeoscoreDestination(location))
                        }
                    )
                }
            }

            entry<Route.GeoscoreDestination> { destination ->
                // Screen-specific ViewModel scoped to this nav entry
                val geoScoreViewModel: GeoScoreViewModel = hiltViewModel()

                AdaptiveNavigationScaffold(
                    highlightedDest = Screen.SAVED.route,
                    onNavigate = onNavigate,
                ) {
                    GeoscoreScreen(
                        location = destination.location,
                        onBackClick = goBack,
                        onHistoricDataClick = {
                            backStack.add(Route.ClimateStatsDestination(destination.location))
                        },
                        frostViewModel = frostViewModel,
                        savedViewModel = savedViewModel,
                        geoScoreViewModel = geoScoreViewModel
                    )
                }
            }

            entry<Route.SearchDestination> {
                // Screen-specific ViewModel scoped to this nav entry
                val searchViewModel: SearchViewModel = hiltViewModel()

                AdaptiveNavigationScaffold(
                    highlightedDest = Screen.HOME.route,
                    onNavigate = onNavigate,
                ) { insets ->
                    SearchScreen(
                        onBackClick = goBack,
                        onLocationSelected = { location ->
                            appViewModel.setSelectedArea(location)
                            backStack.add(Route.MapDestination)
                        },
                        searchViewModel = searchViewModel,
                        modifier = Modifier.padding(insets)
                    )
                }
            }

            entry<Route.ClimateStatsDestination> { destination ->
                AdaptiveNavigationScaffold(
                    highlightedDest = Screen.SAVED.route,
                    onNavigate = onNavigate,
                ) {
                    ClimateStatsScreen(
                        location = destination.location,
                        onBackClick = {
                            // Back button skips Geoscore and returns directly to Map/Saved origin
                            val geoScoreIndex = backStack.indexOfLast { it is Route.GeoscoreDestination }
                            if (geoScoreIndex > 0) {
                                while (backStack.size > geoScoreIndex) {
                                    backStack.removeLastOrNull()
                                }
                            } else {
                                goBack()
                            }
                        },
                        onRapportClick = {
                            backStack.removeLastOrNull()
                        },
                        frostViewModel = frostViewModel
                    )
                }
            }
        }
    )
}
