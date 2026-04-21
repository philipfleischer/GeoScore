package no.uio.ifi.in2000.team20.team20app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepositoryService
import no.uio.ifi.in2000.team20.team20app.domain.model.Location

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
class HomeViewModel(
    private val frostRepository: FrostRepositoryService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /*
     * Kalles når brukeren har valgt et område.
     * Henter værdata for koordinatene og oppdaterer UI-tilstanden.
     */
    fun loadClimateData(location: Location) {
        // No dispatcher needed cause the data layer is responsible for threading
        // FrostRepository uses Ktor which is main-safe and handles IO internally
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val climateData = frostRepository.getClimateData(location.lat, location.lon)
                _uiState.update { it.copy(
                    isLoading = false,
                    climateData = climateData
                ) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Kunne ikke laste inn data for ${location.name}."
                ) }
            }
        }
    }
}