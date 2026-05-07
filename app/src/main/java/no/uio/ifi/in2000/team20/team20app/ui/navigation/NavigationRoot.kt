package no.uio.ifi.in2000.team20.team20app.ui.navigation

import androidx.compose.runtime.Composable
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

/*
Main changes (24.04.2026 adaptive-navigation-impl):
- Replaced goToHome, goToMap and goToSaved with a general onNavigate to reduce repetition (I commented out old code).
- Replaced ScreenScaffold with AdaptiveNavigationScaffold

Hilt refactor:
- Removed all ViewModel, repository, and use-case parameters.
- All ViewModels are now obtained via hiltViewModel() at each nav entry.
*/
@Composable
fun NavigationRoot() {
    // Shared ViewModels — obtained once here and passed down where needed.
    // hiltViewModel() scopes them to the activity's ViewModelStore, so they
    // survive navigation and are the same instance across all entries.
    val appViewModel: AppViewModel = hiltViewModel()
    val frostViewModel: FrostViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val mapViewModel: MapViewModel = hiltViewModel()
    val searchViewModel: SearchViewModel = hiltViewModel()

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
                val savedViewModel: SavedViewModel = hiltViewModel()

                AdaptiveNavigationScaffold(
                    title = Screen.HOME.title,
                    onNavigate = onNavigate,
                    onOpenSettings = goToSettings,
                    highlightedDest = Screen.HOME.route,
                ) { modifier ->
                    HomeScreen(
                        onOpenSearch = goToSearch,
                        modifier = modifier,
                        viewModel = homeViewModel,
                        sharedViewModel = appViewModel,
                        savedViewModel = savedViewModel,
                        frostViewModel = frostViewModel
                    )
                }
            }

            entry<Route.MapDestination> {
                val savedViewModel: SavedViewModel = hiltViewModel()

                AdaptiveNavigationScaffold(
                    title = Screen.MAP.title,
                    onNavigate = onNavigate,
                    onOpenSettings = goToSettings,
                    highlightedDest = Screen.MAP.route
                ) { modifier ->
                    MapScreen(
                        modifier = modifier,
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
                val savedViewModel: SavedViewModel = hiltViewModel()

                AdaptiveNavigationScaffold(
                    title = Screen.SAVED.title,
                    onNavigate = onNavigate,
                    onOpenSettings = goToSettings,
                    highlightedDest = Screen.SAVED.route
                ) { modifier ->
                    SavedScreen(
                        modifier = modifier,
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
                val savedViewModel: SavedViewModel = hiltViewModel()
                val geoScoreViewModel: GeoScoreViewModel = hiltViewModel()

                AdaptiveNavigationScaffold(
                    title = destination.location.name,
                    highlightedDest = Screen.SAVED.route,
                    onNavigate = onNavigate,
                    onBackClick = goBack,
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
                AdaptiveNavigationScaffold(
                    title = "Søk etter en adresse",
                    highlightedDest = Screen.HOME.route,
                    onNavigate = onNavigate,
                    onBackClick = goBack,
                ) { modifier ->
                    SearchScreen(
                        onBackClick = goBack,
                        onLocationSelected = { location ->
                            appViewModel.setSelectedArea(location)
                            backStack.add(Route.MapDestination)
                        },
                        searchViewModel = searchViewModel,
                        modifier = modifier
                    )
                }
            }

            entry<Route.ClimateStatsDestination> { destination ->
                AdaptiveNavigationScaffold(
                    title = destination.location.name,
                    highlightedDest = Screen.SAVED.route,
                    onNavigate = onNavigate,
                    onBackClick = goBack
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
