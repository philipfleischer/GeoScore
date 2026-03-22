package no.uio.ifi.in2000.team20.team20app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
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
    goToHome: () -> Unit,
    goToMap: () -> Unit,
    goToFavorites: () -> Unit,
    currentDestination: NavKey,
    currentComposable: @Composable (Modifier) -> Unit
){
    Scaffold(
        bottomBar = {
            NaturhendelserBottomBar(
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
private fun NaturhendelserBottomBar(
    currentDestination: NavKey?,
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentDestination is Route.HomeDestination,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Hjem") },
            label = { Text("Hjem") }
        )
        NavigationBarItem(
            selected = currentDestination is Route.MapDestination,
            onClick = onMapClick,
            icon = { Icon(Icons.Default.ThumbUp, contentDescription = "Kart") },
            label = { Text("Kart") }
        )
        NavigationBarItem(
            selected = currentDestination is Route.FavoritesDestination,
            onClick = onFavoritesClick,
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritter") },
            label = { Text("Favoritter") }
        )
    }
}

@Preview
@Composable
fun ScreenScaffoldPreview(){
    ScreenScaffold(
        {},{},{}, Route.HomeDestination, {}
    )
}