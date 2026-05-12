package no.uio.ifi.in2000.team20.team20app.ui.screens.result

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.domain.model.GeoScore
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.domain.model.Report
import no.uio.ifi.in2000.team20.team20app.domain.model.scoreToGrade
import no.uio.ifi.in2000.team20.team20app.domain.usecase.GetAiReport
import no.uio.ifi.in2000.team20.team20app.domain.usecase.GetGeoScore
import javax.inject.Inject

/**
 * UiState for GeoScoreScreen
 * @property isScoreLoading Whether the score is currently being calculated
 * @property geoScore The calculated score
 * @property grade The letter grade of the score
 * @property scoreError The error message if the score could not be calculated
 * @property isReportLoading Whether the report is currently being generated
 * @property aiReport The generated report for the score
 * @property reportError The error message if the report could not be generated
 */
data class GeoScoreUiState(
    val isScoreLoading: Boolean = false,
    val geoScore: GeoScore? = null,
    val grade: String = "",
    val scoreError: String? = null,
    val isReportLoading: Boolean = false,
    val aiReport: Report? = null,
    val reportError: String? = null
)

/**
 * ViewModel for GeoScoreScreen
 * @property getGeoScore Use case for calculating the GeoScore
 * @property getAiReport Use case for generating the report
 * @property _uiState The current state of the UI. Private and mutable
 * @property uiState The current state of _uiState collected as a StateFlow
 */
@HiltViewModel
class GeoScoreViewModel @Inject constructor(
    private val getGeoScore: GetGeoScore,
    private val getAiReport: GetAiReport
) : ViewModel() {

    init{
        Log.d("ViewModel", "GeoScoreViewModel created")
    }

    private val _uiState = MutableStateFlow(GeoScoreUiState())
    val uiState: StateFlow<GeoScoreUiState> = _uiState.asStateFlow()

    /**
     * Updates the UI state to reflect loading.
     * Then gets the GeoScore via getGeoScore.
     * This score is then used to get a report via loadReport.
     * The UI state is updated with the results of these.
     * Should score or report fail to load, the UI state reflects so.
     * @param location The location to calculate the score for
     */
    fun load(location: Location) {
        viewModelScope.launch(Dispatchers.IO) { // Moved of main thread, all functions called by this one will inherit this scope. {
            _uiState.update { it.copy(isScoreLoading = true, scoreError = null) }
            runCatching { getGeoScore.calculateGeoScore(location.lat, location.lon) }
                .fold(
                    onSuccess = { score ->
                        _uiState.update {it ->
                            it.copy(
                                isScoreLoading = false,
                                geoScore = score,
                                grade = score.geoScore?.let { scoreToGrade(it) } ?: ""
                            )
                        }
                        loadReport(score)
                    },
                    onFailure = { e ->
                        _uiState.update {
                            //TODO noen exceptions kastes inne i GeoScore algoritmen sjekk hvordan det funker med denne onFailure funksjonen
                            it.copy(isScoreLoading = false, scoreError = e.message ?: "Ukjent feil")
                        }
                    }
                )
        }
    }

    /**
     * Loads the report on the GeoScore via getAiReport.
     * @param score The score to generate a report for
     */
    private suspend fun loadReport(score: GeoScore) {
        _uiState.update { it.copy(isReportLoading = true, reportError = null) }
        runCatching {
            getAiReport.generateReport(score)
        }.fold(
            onSuccess = { report ->
                _uiState.update { it.copy(isReportLoading = false, aiReport = report) }
            },
            onFailure = { e ->
                _uiState.update { it.copy(isReportLoading = false, reportError = e.message) }
            }
        )
    }
}
