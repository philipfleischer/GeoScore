package no.uio.ifi.in2000.team20.team20app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.data.api.FrostClientProvider
import no.uio.ifi.in2000.team20.team20app.data.api.LocationForecsastClientProvider
import no.uio.ifi.in2000.team20.team20app.data.datasource.FrostDataSource
import no.uio.ifi.in2000.team20.team20app.data.datasource.LocationForecastRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.LocationForecastRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.util.Constants

/**
 * ViewModel for HomeScreen.
 *
 * Responsibility:
 * - Fetch hazard overview
 * - Manage HomeUiState
 *
 * Why:
 * Separates UI from data logic.
 */
class HomeViewModel : ViewModel() {

    private val repository = LocationForecastRepository(
        dataSource = LocationForecastRemoteDataSource(
            client = LocationForecsastClientProvider.client
        )
    )

    private val frostRepository = FrostRepository(
        dataSource = FrostDataSource(
            client = FrostClientProvider.client,
            credentials = "${Constants.FROST_CLIENT_ID}:${Constants.FROST_CLIENT_SECRET}"
        )
    )

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /*
     * Kalles når brukeren har valgt et område.
     * Henter værdata for koordinatene og oppdaterer UI-tilstanden.
     */
    fun loadClimateData(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val climateData = frostRepository.getClimateData(location.lat, location.lon)
                _uiState.update { it.copy(
                    isLoading = false,
                    climateData = climateData
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Kunne ikke laste inn data for ${location.name}."
                ) }
            }
        }
    }
}