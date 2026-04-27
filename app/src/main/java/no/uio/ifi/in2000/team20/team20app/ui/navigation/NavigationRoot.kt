package no.uio.ifi.in2000.team20.team20app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import no.uio.ifi.in2000.team20.team20app.data.repository.FavoritesRepository
import no.uio.ifi.in2000.team20.team20app.ui.components.AdaptiveNavigationScaffold
import no.uio.ifi.in2000.team20.team20app.ui.screens.details.AreaDetailsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.details.ClimateStatsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.favorite.FavoriteDetailsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.favorite.FavoritesScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.favorite.FavoritesViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.favorite.FavoritesViewModelFactory
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.map.MapScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.map.MapViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.settings.SettingsScreen
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel
import no.uio.ifi.in2000.team20.team20app.util.Screen

/*
Main changes (24.04.2026 adaptive-navigation-impl):
- Replaced goToHome, goToMap and goToFavorites with a general onNavigate to reduce repetition (I commented out old code).
- Replaced ScreenScaffold with AdaptiveNavigationScaffold
*/
@Composable
fun NavigationRoot(
    appViewModel: AppViewModel,
    searchViewModel: SearchViewModel,
    homeViewModel: HomeViewModel,
    mapViewModel: MapViewModel,
    frostViewModel: FrostViewModel,
    favoritesRepository: FavoritesRepository
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

//    val goToHome: () -> Unit = {backStack.add(Route.HomeDestination)}
//    val goToMap: () -> Unit = {backStack.add(Route.MapDestination)}
//    val goToFavorites: () -> Unit = {backStack.add(Route.FavoritesDestination)}
    val goToSettings: () -> Unit = { backStack.add(Route.SettingsDestination) }
    val goToSearch: () -> Unit = { backStack.add(Route.SearchDestination) }

    NavDisplay(
        backStack = backStack,
        onBack = goBack,
        entryProvider = entryProvider {
            entry<Route.HomeDestination> { // Type parameter. Can't use Screen.XXX.route
                val favoritesViewModel: FavoritesViewModel = viewModel(
                    factory = FavoritesViewModelFactory(favoritesRepository)
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
                        favoritesViewModel = favoritesViewModel,
                        frostViewModel = frostViewModel
                    )
                }
            }

            entry<Route.MapDestination> {
                val favoritesViewModel: FavoritesViewModel = viewModel(
                    factory = FavoritesViewModelFactory(favoritesRepository)
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
                        favoritesViewModel = favoritesViewModel,
                        mapViewModel = mapViewModel
                    )
                }
            }

            entry<Route.FavoritesDestination> {
                val favoritesViewModel: FavoritesViewModel = viewModel(
                    factory = FavoritesViewModelFactory(favoritesRepository)
                )

                AdaptiveNavigationScaffold(
                    title = Screen.FAVORITES.title,
                    onNavigate = onNavigate,
                    onOpenSettings = goToSettings,
                    currentDestination = Screen.FAVORITES.route
                ) { modifier ->
                    FavoritesScreen(
                        modifier = modifier,
                        sharedViewModel = appViewModel,
                        favoritesViewModel = favoritesViewModel,
                        onFavoriteClick = { location ->
                            appViewModel.setSelectedArea(location)
                            backStack.add(Route.FavoriteDetailsDestination(location))
                        }
                    )
                }
            }

            entry<Route.FavoriteDetailsDestination> { destination ->
                val favoritesViewModel: FavoritesViewModel = viewModel(
                    factory = FavoritesViewModelFactory(favoritesRepository)
                )

                FavoriteDetailsScreen(
                    location = destination.location,
                    onBackClick = goBack,
                    onOpenMap = {
                        appViewModel.setSelectedArea(destination.location)
                        backStack.add(Route.MapDestination)
                    },
                    frostViewModel = frostViewModel,
                    favoritesViewModel = favoritesViewModel
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
                        goBack()
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
                    frostViewModel = frostViewModel
                )
            }
        }
    )
}