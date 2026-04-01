package no.uio.ifi.in2000.team20.team20app.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import no.uio.ifi.in2000.team20.team20app.domain.model.Location

@Serializable
sealed interface Route: NavKey {
    /*
 * Denne filen definerer alle navigasjonsdestinasjonene som brukes i appen.
 *
 * Navigation 3 bruker NavKey-objekter i stedet for string-baserte routes
 * (som i Navigation 2). Hver skjerm i applikasjonen representeres derfor
 * av et NavKey-objekt eller en NavKey-dataklasse.
 *
 * @Serializable er nødvendig fordi Navigation 3 kan serialisere disse
 * objektene når de lagres i backstacken.
 *
 * Dataklasser brukes når en skjerm trenger å motta data, for eksempel
 * koordinater eller navn på et område.
 */

    /*
     * Dette er Startskjermen i applikasjonen.
     * Denne viser en oversikt over valgt område og gir tilgang til kart,
     * områdedetaljer og klimastatistikk.
     */
    @Serializable
    object HomeDestination : Route, NavKey

    /*
     * Dette er Kartskjermen.
     * Her skal brukeren kunne navigere i kartet og velge områder
     * for å se risiko for naturhendelser.
     */
    @Serializable
    object MapDestination : Route, NavKey


    /*
     * Dette er Detaljskjerm for et spesifikt område.
     *
     * Denne destinasjonen mottar informasjon om området brukeren har valgt.
     * Disse verdiene brukes senere til å hente data fra API-er
     * (for eksempel klimadata eller risikodata).
     */
    @Serializable
    data class AreaDetailsDestination(
        val location: Location
    ) : Route, NavKey

    /*
     * Dette er Favorittskjermen.
     * Det viser områder brukeren har lagret som favoritter.
     * Brukeren kan velge et favorittområde for å åpne
     * detaljskjermen for dette området.
     */
    @Serializable
    object FavoritesDestination : Route, NavKey

    @Serializable
    data class FavoriteDetailsDestination(
        val location: Location
    ) : Route, NavKey

    /*
     * Dette er skjermen for klimastatistikk.
     *
     * Denne skjermen mottar også informasjon om et område
     * slik at den kan hente historiske eller aggregerte klimadata
     * (for eksempel nedbør, vind eller temperatur).
     */
    @Serializable
    data class ClimateStatsDestination(
        val location: Location
    ) : Route, NavKey

    // DEtte er skjermen for SettingsScreen
    @Serializable
    object SettingsDestination : Route, NavKey

    /*
     * Dette er søkeskjermen.
     * Brukeren søker etter stednavn via Kartverkets API.
     * Valgt sted oppdaterer AppViewModel og sender brukeren tilbake.
     */
    @Serializable
    object SearchDestination : Route, NavKey
}