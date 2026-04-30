package no.uio.ifi.in2000.team20.team20app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import no.uio.ifi.in2000.team20.team20app.data.repository.SavedRepository
import no.uio.ifi.in2000.team20.team20app.ui.components.AdaptiveNavigationScaffold
import no.uio.ifi.in2000.team20.team20app.ui.screens.details.AreaDetailsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.details.ClimateStatsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.saved.GeoscoreScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.saved.SavedScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.saved.SavedViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.saved.SavedViewModelFactory
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.map.MapScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.settings.SettingsScreen
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel
import no.uio.ifi.in2000.team20.team20app.util.Screen

/*
Main changes (24.04.2026 adaptive-navigation-impl):
- Replaced goToHome, goToMap and goToSaved with a general onNavigate to reduce repetition (I commented out old code).
- Replaced ScreenScaffold with AdaptiveNavigationScaffold
*/
@Composable
fun NavigationRoot(
    appViewModel: AppViewModel,
    searchViewModel: SearchViewModel,
    homeViewModel: HomeViewModel,
    frostViewModel: FrostViewModel,
    savedRepository: SavedRepository
){
    //BackStack
    val backStack = rememberNavBackStack(Screen.HOME.route)

    //Navigation-lambdas
    val goBack: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    val onNavigate: (Screen) -> Unit = {
        screen -> screen.route.let { backStack.add(it) }
    }

    val goToSettings: () -> Unit = { backStack.add(Route.SettingsDestination) }
    val goToSearch: () -> Unit = { backStack.add(Route.SearchDestination) }

    NavDisplay(
        backStack = backStack,
        onBack = goBack,
        entryProvider = entryProvider {
            entry<Route.HomeDestination> { // Type parameter. Can't use Screen.XXX.route
                val savedViewModel: SavedViewModel = viewModel(
                    factory = SavedViewModelFactory(savedRepository)
                )

                AdaptiveNavigationScaffold(
                    title = Screen.HOME.title,
                    onNavigate = onNavigate,
                    onOpenSettings = goToSettings,
                    currentDestination = Screen.HOME.route
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
                val savedViewModel: SavedViewModel = viewModel(
                    factory = SavedViewModelFactory(savedRepository)
                )

                AdaptiveNavigationScaffold(
                    title = Screen.MAP.title,
                    onNavigate = onNavigate,
                    onOpenSettings = goToSettings,
                    currentDestination = Screen.MAP.route
                ) { modifier ->
                    MapScreen(
                        modifier = modifier,
                        sharedViewModel = appViewModel,
                        savedViewModel = savedViewModel
                    )
                }
            }

            entry<Route.SavedDestination> {
                val savedViewModel: SavedViewModel = viewModel(
                    factory = SavedViewModelFactory(savedRepository)
                )

                AdaptiveNavigationScaffold(
                    title = Screen.SAVED.title,
                    onNavigate = onNavigate,
                    onOpenSettings = goToSettings,
                    currentDestination = Screen.SAVED.route
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
                val savedViewModel: SavedViewModel = viewModel(
                    factory = SavedViewModelFactory(savedRepository)
                )

                GeoscoreScreen(
                    location = destination.location,
                    onBackClick = goBack,
                    onHistoricDataClick = {
                        backStack.removeLastOrNull()
                        backStack.add(Route.ClimateStatsDestination(destination.location))
                    },
                    frostViewModel = frostViewModel,
                    savedViewModel = savedViewModel
                )
            }

            entry<Route.SettingsDestination> {
                SettingsScreen(
                    onBackClick = goBack
                )
            }

            entry<Route.SearchDestination> {
                SearchScreen(
                    onBackClick = goBack,
                    onLocationSelected = { location ->
                        appViewModel.setSelectedArea(location)
                        backStack.add(Route.GeoscoreDestination(location))
                    },
                    searchViewModel = searchViewModel
                )
            }

            entry<Route.AreaDetailsDestination> { destination ->
                AreaDetailsScreen(
                    location = destination.location,
                    onBackClick = goBack,
                    onOpenClimateStats = {
                        backStack.add(
                            Route.ClimateStatsDestination(
                                destination.location
                            )
                        )
                    }
                )
            }

            entry<Route.ClimateStatsDestination> { destination ->
                ClimateStatsScreen(
                    location = destination.location,
                    onBackClick = goBack,
                    onRapportClick = {
                        backStack.add(Route.GeoscoreDestination(destination.location))
                    },
                    frostViewModel = frostViewModel
                )
            }
        }
    )
}