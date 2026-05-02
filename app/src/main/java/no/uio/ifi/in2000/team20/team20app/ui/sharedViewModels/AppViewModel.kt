package no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.team20.team20app.domain.model.Location

//TODO: Dokumenter at AppState nå er en felles viewModel. Dette er gjort fordi selectedLocation potensielt skal aksesseres av repoer som skal hente data for stedet.
//
class AppViewModel: ViewModel() {

    // USing Oslo as default, security choice we made.
    // Appen vår starter med forhåndsvalgt område i stedet for å hente brukerens eksakte posisjon.
    // Dette gjør at appen fungerer uten lokasjonstillatelser og er mer personvernvennlig som standard.
    private val _selectedLocation = MutableStateFlow(
        Location(
            "Oslo",
            municipality = null,
            county = null,
            lat = 59.9139,
            lon = 10.7522
        )
    )
    val selectedLocation: StateFlow<Location> = _selectedLocation.asStateFlow()

    val selectedAreaName get() = _selectedLocation.value.name
    val selectedLatitude get() = _selectedLocation.value.lat
    val selectedLongitude get() = _selectedLocation.value.lon

    fun setSelectedArea(location: Location) {
        _selectedLocation.value = location
    }
}