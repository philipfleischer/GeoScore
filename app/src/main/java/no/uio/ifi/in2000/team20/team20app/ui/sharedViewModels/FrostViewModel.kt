package no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepositoryService
import no.uio.ifi.in2000.team20.team20app.domain.model.Location

data class FrostUiState(
    val isLoading: Boolean = false,
    // Temperature normals (Jan–Dec, index 0–11), aggregated from 1991-2020 raw monthly data
    val temperatureMean: List<Double>? = null,
    val temperatureMax: List<Double>? = null,
    val temperatureMin: List<Double>? = null,
    val temperatureError: String? = null,
    // Wind normals (Jan–Dec, index 0–11)
    val windMean: List<Double>? = null,
    val windMaxSpeed: List<Double>? = null,
    val windMaxGust: List<Double>? = null,
    val windError: String? = null,
    // Sunshine hours per month (Jan–Dec, index 0–11)
    val sunshineHours: List<Double>? = null,
    val sunshineStationName: String? = null,
    val sunshineDistanceKm: Double? = null,
    val sunshineError: String? = null,
    // Snow depth (Jan–Dec, index 0–11), aggregated from 1991-2020 raw monthly data
    val snowMean: List<Double>? = null,
    val snowMax: List<Double>? = null,
    val snowError: String? = null,
    // Precipitation (Jan–Dec, index 0–11), aggregated from 1991-2020 raw monthly data
    // precipitationDays = rainy days per month, precipitationMean = max daily precipitation in mm
    val precipitationMean: List<Double>? = null,
    val precipitationDays: List<Double>? = null,
    val precipitationError: String? = null,
)

/**
 * Shared ViewModel for Frost climate data.
 *
 * Responsibility:
 * - Load climate data for a given location, launching all parameters concurrently
 * - Manage FrostUiState with per-parameter loading and error fields
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
            _uiState.update { it.copy(isLoading = true) }
            coroutineScope {
                // All parameters fetched concurrently, each fails independently via Result
                val tempDeferred = async { repo.getTemperatureData(location.lat, location.lon) }
                val windDeferred = async { repo.getWindData(location.lat, location.lon) }
                val sunDeferred  = async { repo.getSunshineData(location.lat, location.lon) }
                val snowDeferred = async { repo.getSnowData(location.lat, location.lon) }
                val precipitationDeferred = async { repo.getPrecipitationData(location.lat, location.lon) }

                val tempResult = tempDeferred.await()
                val windResult = windDeferred.await()
                val sunResult  = sunDeferred.await()
                val snowResult = snowDeferred.await()
                val precipitationResult = precipitationDeferred.await()

                val tempData = tempResult.getOrNull()
                val windData = windResult.getOrNull()
                val snowData = snowResult.getOrNull()
                val precipData = precipitationResult.getOrNull()

                _uiState.update {
                    it.copy(
                        isLoading        = false,
                        temperatureMean  = tempData?.first,
                        temperatureMax   = tempData?.second,
                        temperatureMin   = tempData?.third,
                        temperatureError = tempResult.exceptionOrNull()?.message,
                        windMean         = windData?.first,
                        windMaxSpeed     = windData?.second,
                        windMaxGust      = windData?.third,
                        windError        = windResult.exceptionOrNull()?.message,
                        sunshineHours    = sunResult.getOrNull()?.first,
                        sunshineStationName = sunResult.getOrNull()?.second,
                        sunshineDistanceKm  = sunResult.getOrNull()?.third,
                        sunshineError    = sunResult.exceptionOrNull()?.message,
                        snowMean           = snowData?.first,
                        snowMax            = snowData?.second,
                        snowError          = snowResult.exceptionOrNull()?.message,
                        precipitationMean  = precipData?.second,
                        precipitationDays  = precipData?.first,
                        precipitationError = precipitationResult.exceptionOrNull()?.message,
                    )
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