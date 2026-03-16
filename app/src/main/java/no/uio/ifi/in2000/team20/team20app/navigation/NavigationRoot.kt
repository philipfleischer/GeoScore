package no.uio.ifi.in2000.team20.team20app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import no.uio.ifi.in2000.team20.team20app.ScreenScaffold
import no.uio.ifi.in2000.team20.team20app.ui.screens.details.AreaDetailsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.details.ClimateStatsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.favorite.FavoritesScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.map.MapScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.settings.SettingsScreen

@Composable
fun NavigationRoot(appState: AppState){

    val backStack = rememberNavBackStack(Route.HomeDestination)
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
                ScreenScaffold(
                    goToHome = goToHome,
                    goToMap = goToMap,
                    goToFavorites = goToFavorites,
                    currentDestination = Route.HomeDestination
                ) {
                    HomeScreen(
                        areaName = appState.selectedAreaName,
                        onOpenMap = { backStack.add(Route.MapDestination) },
                        onOpenDetails = {
                            backStack.add(
                                Route.AreaDetailsDestination(
                                    areaName = appState.selectedAreaName,
                                    latitude = appState.selectedLatitude,
                                    longitude = appState.selectedLongitude
                                )
                            )
                        },
                        onOpenClimateStats = {
                            backStack.add(
                                Route.ClimateStatsDestination(
                                    areaName = appState.selectedAreaName,
                                    latitude = appState.selectedLatitude,
                                    longitude = appState.selectedLongitude
                                )
                            )
                        },
                        onOpenSettings = {
                            backStack.add(Route.SettingsDestination)
                        }
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
                        selectedAreaName = appState.selectedAreaName,
                        onAreaSelected = { name, lat, lon ->
                            appState.setSelectedArea(name, lat, lon)
                        },
                        onOpenDetails = { name, lat, lon ->
                            appState.setSelectedArea(name, lat, lon)
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
                            appState.setSelectedArea(name, lat, lon)
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