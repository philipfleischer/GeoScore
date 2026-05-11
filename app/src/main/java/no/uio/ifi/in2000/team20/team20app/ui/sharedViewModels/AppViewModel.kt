package no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.util.Constants.DEFAULT_LATITUDE
import no.uio.ifi.in2000.team20.team20app.util.Constants.DEFAULT_LONGITUDE
import no.uio.ifi.in2000.team20.team20app.util.Constants.KEY_SELECTED_LOCATION
import javax.inject.Inject

//TODO: Dokumenter at AppState nå er en felles viewModel. Dette er gjort fordi selectedLocation aksesseres av flere skjermer og repoer som skal hente data for stedet.
//
@HiltViewModel
class AppViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {

    // App starts with no selected location, except for in the context of a OS-initiated process death
    val selectedLocation = state.getStateFlow<Location?>(KEY_SELECTED_LOCATION, null)

    //TODO: Why is this here? Consider moving...
    val defaultCameraPosition = LatLng(
        DEFAULT_LATITUDE,
        DEFAULT_LONGITUDE
    )

    fun setSelectedArea(location: Location) {
        state[KEY_SELECTED_LOCATION] = location
    }
}