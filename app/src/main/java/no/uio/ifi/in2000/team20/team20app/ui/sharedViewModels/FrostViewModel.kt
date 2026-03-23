package no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepositoryService
import no.uio.ifi.in2000.team20.team20app.ui.screens.climate.FrostUiState

/**
 * ViewModel for climate/Frost data.
 *
 * Responsibility:
 * - Fetch climate data from FrostRepository
 * - Manage FrostUiState (loading, success, error)
 *
 * Why:
 * Separates UI from data logic.
 * Accepts FrostRepositoryService so it can be tested with a fake repository.
 */
class FrostViewModel(
    private val repo: FrostRepositoryService
) : ViewModel() {

    private val _uiState = MutableStateFlow(FrostUiState())
    val uiState: StateFlow<FrostUiState> = _uiState.asStateFlow()

    fun loadClimateData(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val climateData = repo.getClimateData(lat, lon)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        climateData = climateData
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Kunne ikke laste inn klimadata."
                    )
                }
            }
        }
    }
}
