package no.uio.ifi.in2000.team20.team20app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.components.ScreenScaffold
import no.uio.ifi.in2000.team20.team20app.ui.screens.details.AreaDetailsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.details.ClimateStatsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.favorite.FavoritesScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.map.MapScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.settings.SettingsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchScreen
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel

@Composable
fun NavigationRoot(appViewModel: AppViewModel){
    //BackStack
    val backStack = rememberNavBackStack(Route.HomeDestination)

    //Navigation-lambdas
    val goBack: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }
    val goToHome: () -> Unit = {backStack.add(Route.HomeDestination)}
    val goToMap: () -> Unit = {backStack.add(Route.MapDestination)}
    val goToFavorites: () -> Unit = {backStack.add(Route.FavoritesDestination)}
    val goToSettings: () -> Unit = { backStack.add(Route.SettingsDestination) }
    val goToSearch: () -> Unit = { backStack.add(Route.SearchDestination) }

    NavDisplay(
        backStack = backStack,
        onBack = goBack,
        entryProvider = entryProvider {
            entry<Route.HomeDestination> {
                val homeViewModel: HomeViewModel = viewModel()
                ScreenScaffold(
                    title = "Geomerking",
                    goToHome = goToHome,
                    goToMap = goToMap,
                    goToFavorites = goToFavorites,
                    onOpenSettings = goToSettings,
                    currentDestination = Route.HomeDestination
                ) { modifier ->
                    HomeScreen(
                        onOpenSearch = goToSearch,
                        modifier = modifier,
                        viewModel = homeViewModel,
                        sharedViewModel = appViewModel
                    )
                }
            }

            entry<Route.MapDestination> {
                ScreenScaffold(
                    title = "Kart",
                    goToHome = goToHome,
                    goToMap = goToMap,
                    goToFavorites = goToFavorites,
                    onOpenSettings = goToSettings,
                    currentDestination = Route.MapDestination
                ) { modifier ->
                    MapScreen(
                        modifier = modifier
                    )
                }
            }

            entry<Route.FavoritesDestination> {
                ScreenScaffold(
                    title = "Lagret",
                    goToHome = goToHome,
                    goToMap = goToMap,
                    goToFavorites = goToFavorites,
                    onOpenSettings = goToSettings,
                    currentDestination = Route.FavoritesDestination
                ) { modifier ->
                    FavoritesScreen(
                        modifier = modifier,
                        appViewModel

                    )
                }
            }

            // NEW SETTINGS ENTRY
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
                        goBack()
                    }
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
                    onBackClick = goBack
                )
            }
        }
    )
}