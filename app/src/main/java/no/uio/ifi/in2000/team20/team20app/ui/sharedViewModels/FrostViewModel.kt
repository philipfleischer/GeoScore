package no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepositoryService
import no.uio.ifi.in2000.team20.team20app.domain.model.FrostStats
import no.uio.ifi.in2000.team20.team20app.domain.model.Location

data class FrostUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val frostStats: FrostStats? = null
)

/**
 * Shared ViewModel for Frost climate data.
 *
 * Responsibility:
 * - Load FrostStats for a given location
 * - Manage FrostUiState (loading, success, error)
 *
 * Why:
 * Shared across HomeScreen and FavoriteDetailsScreen so both tabs observe
 * the same state without duplicating fetch logic.
 * Accepts FrostRepositoryService so it can be tested with a fake repository.
 */
class FrostViewModel(
    private val repo: FrostRepositoryService
) : ViewModel() {

    private val _uiState = MutableStateFlow(FrostUiState())
    val uiState: StateFlow<FrostUiState> = _uiState.asStateFlow()

    fun loadFrostStats(location: Location) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val frostStats = repo.getFrostStats(location.lat, location.lon)
                _uiState.update { it.copy(isLoading = false, frostStats = frostStats) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                Log.e("FrostViewModel", "getFrostStats failed", e)
                _uiState.update {
                    it.copy(isLoading = false, error = "Kunne ikke laste inn klimadata.")
                }
            }
        }
    }
}

class FrostViewModelFactory(
    private val repo: FrostRepositoryService
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FrostViewModel(repo) as T
    }
}