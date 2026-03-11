package no.uio.ifi.in2000.team20.team20app.navigation

import androidx.compose.foundation.layout.padding
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
    //Initialize viewmodels, httpclients etc here?
    NavDisplay(
        backStack = appState.backStack,
        onBack = { appState.goBack() },
        modifier = modifier,
        entryProvider = entryProvider {
            entry<HomeDestination> {
                HomeScreen(
                    areaName = appState.selectedAreaName,
                    onOpenMap = { appState.navigateTo(MapDestination) },
                    onOpenDetails = {
                        appState.navigateTo(
                            AreaDetailsDestination(
                                areaName = appState.selectedAreaName,
                                latitude = appState.selectedLatitude,
                                longitude = appState.selectedLongitude
                            )
                        )
                    },
                    onOpenClimateStats = {
                        appState.navigateTo(
                            ClimateStatsDestination(
                                areaName = appState.selectedAreaName,
                                latitude = appState.selectedLatitude,
                                longitude = appState.selectedLongitude
                            )
                        )
                    }
                )
            }

            entry<MapDestination> {
                MapScreen(
                    selectedAreaName = appState.selectedAreaName,
                    onAreaSelected = { name, lat, lon ->
                        appState.setSelectedArea(name, lat, lon)
                    },
                    onOpenDetails = { name, lat, lon ->
                        appState.setSelectedArea(name, lat, lon)
                        appState.navigateTo(AreaDetailsDestination(name, lat, lon))
                    }
                )
            }

            entry<FavoritesDestination> {
                FavoritesScreen(
                    onFavoriteSelected = { name, lat, lon ->
                        appState.setSelectedArea(name, lat, lon)
                        appState.navigateTo(AreaDetailsDestination(name, lat, lon))
                    }
                )
            }

            entry<AreaDetailsDestination> { destination ->
                AreaDetailsScreen(
                    areaName = destination.areaName,
                    latitude = destination.latitude,
                    longitude = destination.longitude,
                    onBackClick = { appState.goBack() },
                    onOpenClimateStats = {
                        appState.navigateTo(
                            ClimateStatsDestination(
                                areaName = destination.areaName,
                                latitude = destination.latitude,
                                longitude = destination.longitude
                            )
                        )
                    }
                )
            }

            entry<ClimateStatsDestination> { destination ->
                ClimateStatsScreen(
                    areaName = destination.areaName,
                    latitude = destination.latitude,
                    longitude = destination.longitude,
                    onBackClick = { appState.goBack() }
                )
            }
        }
    )
}