package no.uio.ifi.in2000.team20.team20app.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import no.uio.ifi.in2000.team20.team20app.ui.navigation.Route

/*
Enum to hold attributes to different screens such as title, route and icon
contentDescription can be added if needed
*/
enum class Screen(
    val route: NavKey,
    val title: String,
    val icon: ImageVector,
//    val contentDescription: String
) {
    HOME(Route.HomeDestination, "Søk", Icons.Default.LocationOn),
    MAP(Route.MapDestination, "Kart", Icons.Default.Map),
    SAVED(Route.SavedDestination, "Lagret", Icons.Default.Bookmark)
}