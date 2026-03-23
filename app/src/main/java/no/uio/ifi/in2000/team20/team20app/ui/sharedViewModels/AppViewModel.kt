package no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import no.uio.ifi.in2000.team20.team20app.domain.model.CreatedLocation

//TODO: Dokumenter at AppState nå er en felles viewModel. Dette er gjort fordi selectedLocation potensielt skal aksesseres av repoer som skal hente data for stedet.
//
class AppViewModel: ViewModel() {

    var selectedLocation: CreatedLocation by mutableStateOf(
        CreatedLocation(
            "Oslo",
            59.9139,
            10.7522,
            null
        )
    )
        private set

    val selectedAreaName get() = selectedLocation.name
    val selectedLatitude get() = selectedLocation.latitude
    val selectedLongitude get() = selectedLocation.longitude

    fun setSelectedArea(name: String, latitude: Double, longitude: Double) {
        selectedLocation = CreatedLocation(name, latitude, longitude, null)
    }
}