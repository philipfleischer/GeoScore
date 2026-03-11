package no.uio.ifi.in2000.team20.team20app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import no.uio.ifi.in2000.team20.team20app.ui.screens.details.AreaDetailsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.details.ClimateStatsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.favorite.FavoritesScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.map.MapScreen

@Composable
fun NavigationRoot(appState: AppState, modifier: Modifier){

    val backStack = rememberNavBackStack(Route.HomeDestination)
    val goBack: () -> Unit = {backStack.removeAt(backStack.lastIndex)}

    NavDisplay(
        backStack = backStack,
        onBack = goBack,
        modifier = modifier,
        entryProvider = entryProvider {
            entry<Route.HomeDestination> {
                HomeScreen(
                    areaName = appState.selectedAreaName,
                    onOpenMap = {backStack.add(Route.MapDestination)},
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
                    }
                )
            }

            entry<Route.MapDestination> {
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

            entry<Route.FavoritesDestination> {
                FavoritesScreen(
                    onFavoriteSelected = { name, lat, lon ->
                        appState.setSelectedArea(name, lat, lon)
                        backStack.add(Route.AreaDetailsDestination(name, lat, lon))
                    }
                )
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