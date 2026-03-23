package no.uio.ifi.in2000.team20.team20app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import no.uio.ifi.in2000.team20.team20app.ui.components.ScreenScaffold
import no.uio.ifi.in2000.team20.team20app.ui.screens.details.AreaDetailsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.details.ClimateStatsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.favorite.FavoritesScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.map.MapScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.settings.SettingsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeViewModel
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

    NavDisplay(
        backStack = backStack,
        onBack = goBack,
        entryProvider = entryProvider {
            entry<Route.HomeDestination> {
                val homeViewModel: HomeViewModel = viewModel()
                ScreenScaffold(
                    goToHome = goToHome,
                    goToMap = goToMap,
                    goToFavorites = goToFavorites,
                    currentDestination = Route.HomeDestination
                ) {
                    HomeScreen(
                        areaName = appViewModel.selectedAreaName,
                        latitude = appViewModel.selectedLatitude,
                        longitude = appViewModel.selectedLongitude,
                        onOpenMap = goToMap,
                        onOpenDetails = {
                            backStack.add(
                                Route.AreaDetailsDestination(
                                    areaName = appViewModel.selectedAreaName,
                                    latitude = appViewModel.selectedLatitude,
                                    longitude = appViewModel.selectedLongitude
                                )
                            )
                        },
                        onOpenClimateStats = {
                            backStack.add(
                                Route.ClimateStatsDestination(
                                    areaName = appViewModel.selectedAreaName,
                                    latitude = appViewModel.selectedLatitude,
                                    longitude = appViewModel.selectedLongitude
                                )
                            )
                        },
                        onOpenSettings = goToSettings,
                        viewModel = homeViewModel,
                        sharedViewModel = appViewModel
                    )
                }
            }

            entry<Route.MapDestination> {
                ScreenScaffold(
                    goToHome = goToHome,
                    goToMap = goToMap,
                    goToFavorites = goToFavorites,
                    currentDestination = Route.MapDestination
                ) {
                    MapScreen(
                        selectedAreaName = appViewModel.selectedAreaName,
                        onAreaSelected = { name, lat, lon ->
                            appViewModel.setSelectedArea(name, lat, lon)
                        },
                        onOpenDetails = { name, lat, lon ->
                            appViewModel.setSelectedArea(name, lat, lon)
                            backStack.add(Route.AreaDetailsDestination(name, lat, lon))
                        }
                    )
                }
            }

            entry<Route.FavoritesDestination> {
                ScreenScaffold(
                    goToHome = goToHome,
                    goToMap = goToMap,
                    goToFavorites = goToFavorites,
                    currentDestination = Route.FavoritesDestination
                ) {
                    FavoritesScreen(
                        onFavoriteSelected = { name, lat, lon ->
                            appViewModel.setSelectedArea(name, lat, lon)
                            backStack.add(Route.AreaDetailsDestination(name, lat, lon))
                        }
                    )
                }
            }

            // NEW SETTINGS ENTRY
            entry<Route.SettingsDestination> {
                ScreenScaffold(
                    goToHome = goToHome,
                    goToMap = goToMap,
                    goToFavorites = goToFavorites,
                    currentDestination = Route.HomeDestination
                ) {
                    SettingsScreen(
                        onBackClick = goBack
                    )
                }
            }

            entry<Route.AreaDetailsDestination> { destination ->
                AreaDetailsScreen(
                    areaName = destination.areaName,
                    latitude = destination.latitude,
                    longitude = destination.longitude,
                    onBackClick = goBack,
                    onOpenClimateStats = {
                        backStack.add(
                            Route.ClimateStatsDestination(
                                areaName = destination.areaName,
                                latitude = destination.latitude,
                                longitude = destination.longitude
                            )
                        )
                    }
                )
            }

            entry<Route.ClimateStatsDestination> { destination ->
                ClimateStatsScreen(
                    areaName = destination.areaName,
                    latitude = destination.latitude,
                    longitude = destination.longitude,
                    onBackClick = goBack
                )
            }
        }
    )
}