package no.uio.ifi.in2000.team20.team20app.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import no.uio.ifi.in2000.team20.team20app.domain.model.Location

@Serializable
sealed interface Route: NavKey {
    /*
     * This file defines all navigation destinations used in the app.
     *
     * Navigation 3 uses NavKey objects instead of string-based routes
     * (as in Navigation 2). Each screen in the application is therefore
     * represented by a NavKey object or a NavKey data class.
     *
     * @Serializable is required because Navigation 3 can serialize these
     * objects when saving them in the back stack.
     *
     * Data classes are used when a screen needs to receive data, for example
     * coordinates or the name of an area.
     */

    /*
     * This is the Home screen of the application.
     * The landing screen with an introduction message and search bar.
     * Contains a navigation bar for accessing other app sections.
     */
    @Serializable
    object HomeDestination : Route, NavKey

    /*
     * This is the Map screen.
     * Here the user should be able to navigate on the map and select areas
     * to see the risk of natural events.
     */
    @Serializable
    object MapDestination : Route, NavKey

    /*
     * This is the Saved screen.
     * It shows areas the user has saved.
     */
    @Serializable
    object SavedDestination : Route, NavKey

    @Serializable
    data class GeoscoreDestination(
        val location: Location
    ) : Route, NavKey

    /*
     * This is the Climate Statistics screen.
     *
     * This screen also receives information about an area
     * so that it can fetch historical or aggregated climate data
     * (for example precipitation, wind or temperature).
     */
    @Serializable
    data class ClimateStatsDestination(
        val location: Location
    ) : Route, NavKey

    /*
     * This is the Search screen.
     * The user searches for place names via the Kartverket API.
     * The selected location updates AppViewModel and sends the user back.
     */
    @Serializable
    object SearchDestination : Route, NavKey
}