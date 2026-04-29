package no.uio.ifi.in2000.team20.team20app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import no.uio.ifi.in2000.team20.team20app.ui.navigation.Route

@Composable
fun ScreenScaffold(
    title: String,
    goToHome: () -> Unit,
    goToMap: () -> Unit,
    goToSaved: () -> Unit,
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
                onSavedClick = goToSaved
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
    onSavedClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            NavigationBarItem(
                selected = currentDestination is Route.HomeDestination,
                onClick = onHomeClick,
                icon = { Icon(Icons.Default.LocationOn, contentDescription = "Søk") },
                label = { Text("Søk") },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
            NavigationBarItem(
                selected = currentDestination is Route.MapDestination,
                onClick = onMapClick,
                icon = { Icon(Icons.Default.Map, contentDescription = "Kart") },
                label = { Text("Kart") },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
            NavigationBarItem(
                selected = currentDestination is Route.SavedDestination,
                onClick = onSavedClick,
                icon = { Icon(Icons.Default.Bookmarks, contentDescription = "Lagret") },
                label = { Text("Lagret") },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}

@Preview
@Composable
fun ScreenScaffoldPreview(){
    ScreenScaffold(
        title = "Geomerking",
        goToHome = {},
        goToMap = {},
        goToSaved = {},
        onOpenSettings = {},
        currentDestination = Route.HomeDestination,
        currentComposable = {}
    )
}