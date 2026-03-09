package no.uio.ifi.in2000.team20.team20app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route: NavKey {
    @Serializable
    data object HomeScreen: Route, NavKey
}