package no.uio.ifi.in2000.team20.team20app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import no.uio.ifi.in2000.team20.team20app.navigation.HomeDestination
import no.uio.ifi.in2000.team20.team20app.navigation.MapDestination
import no.uio.ifi.in2000.team20.team20app.navigation.AppState
import no.uio.ifi.in2000.team20.team20app.navigation.AreaDetailsDestination
import no.uio.ifi.in2000.team20.team20app.navigation.ClimateStatsDestination
import no.uio.ifi.in2000.team20.team20app.navigation.FavoritesDestination
import no.uio.ifi.in2000.team20.team20app.ui.screens.details.AreaDetailsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.details.ClimateStatsScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.favorite.FavoritesScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeScreen
import no.uio.ifi.in2000.team20.team20app.ui.screens.map.MapScreen


@Composable
fun NaturhendelserApp() {
    val appState = remember { AppState() }
    val currentDestination = appState.backStack.lastOrNull()

    Scaffold(
        bottomBar = {
            NaturhendelserBottomBar(
                currentDestination = currentDestination,
                onHomeClick = { appState.navigateTo(HomeDestination) },
                onMapClick = { appState.navigateTo(MapDestination) },
                onFavoritesClick = { appState.navigateTo(FavoritesDestination) }
            )
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = appState.backStack,
            onBack = { appState.goBack() },
            modifier = Modifier.padding(innerPadding),
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
}

@Composable
private fun NaturhendelserBottomBar(
    currentDestination: NavKey?,
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentDestination is HomeDestination,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Hjem") },
            label = { Text("Hjem") }
        )
        NavigationBarItem(
            selected = currentDestination is MapDestination,
            onClick = onMapClick,
            icon = { Icon(Icons.Default.ThumbUp, contentDescription = "Kart") },
            label = { Text("Kart") }
        )
        NavigationBarItem(
            selected = currentDestination is FavoritesDestination,
            onClick = onFavoritesClick,
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritter") },
            label = { Text("Favoritter") }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NaturhendelserAppPreview() {
    MaterialTheme {
        NaturhendelserApp()
    }
}