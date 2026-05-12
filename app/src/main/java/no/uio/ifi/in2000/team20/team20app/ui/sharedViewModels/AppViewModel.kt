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

/**
 * Shared ViewModel for App-level selectedLocation state.
 *
 * Reponsibility:
 * - Provide the selected location for Ui and API-calls
 * - Presist the selected location to a SavedStateHandle
 *
 * Why:
 * Shared across multiple screens to achieve the flow we want for the app
 *
 * @param state SavedStateHandle used to persist data across configuration and process death.
 * @property selectedLocation MutableStateFlow containing the selected location. This is accessed by the map and for getting data.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {

    // App starts with no selected location, except for in the context of an OS-initiated process death
    val selectedLocation = state.getStateFlow<Location?>(KEY_SELECTED_LOCATION, null)

    fun setSelectedArea(location: Location) {
        state[KEY_SELECTED_LOCATION] = location
    }
}