package no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import no.uio.ifi.in2000.team20.team20app.domain.model.Location

//TODO: Dokumenter at AppState nå er en felles viewModel. Dette er gjort fordi selectedLocation potensielt skal aksesseres av repoer som skal hente data for stedet.
//
class AppViewModel: ViewModel() {

    // USing Oslo as default, security choice we made.
    // Appen vår starter med forhåndsvalgt område i stedet for å hente brukerens eksakte posisjon.
    // Dette gjør at appen fungerer uten lokasjonstillatelser og er mer personvernvennlig som standard.
    var selectedLocation: Location by mutableStateOf(
        Location(
            "Oslo",
            municipality = null,
            county = null,
            lat = 59.9139,
            lon = 10.7522
        )
    )
        private set

    val selectedAreaName get() = selectedLocation.name
    val selectedLatitude get() = selectedLocation.lat
    val selectedLongitude get() = selectedLocation.lon

    fun setSelectedArea(location: Location) {
        selectedLocation = location
    }
}