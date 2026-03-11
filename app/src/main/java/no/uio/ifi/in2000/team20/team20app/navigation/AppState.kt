package no.uio.ifi.in2000.team20.team20app.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavKey

/*
 * AppState klassen håndterer navigasjonstilstanden i applikasjonen.
 *
 * I Navigation 3 styrer utvikleren selv backstacken i stedet for å bruke
 * NavController, som i Navigation 2.
 * Denne klassen lagrer derfor:
 * - Navigasjonsstakken (backStack) og
 * - Informasjon om valgt område i appen
 *
 * Tilstanden er lagret med Compose-state slik at UI
 * oppdateres automatisk når verdiene endres.
 */
class AppState {
    /*
     * Backstack for navigasjon i appen.
     * Starter alltid på HomeDestination (startskjermen).
     */
    val backStack = mutableStateListOf<NavKey>(Route.HomeDestination)

    /*
     * Informasjon om området brukeren har valgt.
     * Disse verdiene brukes av flere skjermer, som:
     * - kart
     * - områdedetaljer
     * - klimastatistikk
     */
    var selectedAreaName by mutableStateOf("Oslo")
    var selectedLatitude by mutableStateOf(59.9139)
    var selectedLongitude by mutableStateOf(10.7522)

    /*
     * Legger til en ny destinasjon på backstacken.
     * Dette tilsvarer altså å navigere til en ny skjerm.
     */
    fun navigateTo(destination: NavKey) {
        backStack.add(destination)
    }

    /*
     * Dette er navigasjon tilbake til forrige skjerm.
     * Fjerner siste element fra backstacken hvis vi ikke er på startskjermen.
     */
    fun goBack() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    /*
     * Denne metoden oppdaterer informasjon om valgt område.
     * Kalles for eksempel når brukeren velger et område i kartet.
     */
    fun setSelectedArea(name: String, latitude: Double, longitude: Double) {
        selectedAreaName = name
        selectedLatitude = latitude
        selectedLongitude = longitude
    }
}