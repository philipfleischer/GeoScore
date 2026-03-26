package no.uio.ifi.in2000.team20.team20app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import no.uio.ifi.in2000.team20.team20app.ui.navigation.Route

@Composable
fun ScreenScaffold(
    title: String,
    goToHome: () -> Unit,
    goToMap: () -> Unit,
    goToFavorites: () -> Unit,
    onOpenSettings: () -> Unit,
    currentDestination: NavKey,
    currentComposable: @Composable (Modifier) -> Unit
){
    Scaffold(
        topBar = {
            SharedTopAppBar(
                title = title,
                onOpenSettings = onOpenSettings
            )
        },
        bottomBar = {
            SharedBottomBar(
                currentDestination = currentDestination,
                onHomeClick = goToHome,
                onMapClick = goToMap,
                onFavoritesClick = goToFavorites
            )
        }
    ) {innerPadding ->
        currentComposable(Modifier.padding(innerPadding))
    }
}

@Composable
private fun SharedBottomBar(
    currentDestination: NavKey?,
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentDestination is Route.HomeDestination,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Søk") },
            label = { Text("Søk") }
        )
        NavigationBarItem(
            selected = currentDestination is Route.MapDestination,
            onClick = onMapClick,
            icon = { Icon(Icons.Default.Map, contentDescription = "Kart") },
            label = { Text("Kart") }
        )
        NavigationBarItem(
            selected = currentDestination is Route.FavoritesDestination,
            onClick = onFavoritesClick,
            icon = { Icon(Icons.Default.Bookmarks, contentDescription = "Lagret") },
            label = { Text("Lagret") }
        )
    }
}

@Preview
@Composable
fun ScreenScaffoldPreview(){
    ScreenScaffold(
        title = "Geomerking",
        goToHome = {},
        goToMap = {},
        goToFavorites = {},
        onOpenSettings = {},
        currentDestination = Route.HomeDestination,
        currentComposable = {}
    )
}