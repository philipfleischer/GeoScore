package no.uio.ifi.in2000.team20.team20app.ui.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.domain.model.GeoScore
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.domain.model.Report
import no.uio.ifi.in2000.team20.team20app.domain.model.scoreToGrade
import no.uio.ifi.in2000.team20.team20app.domain.usecase.GetGeoScore

data class GeoScoreUiState (
        val geoScore: GeoScore? = null,
        val grade: String = "",
        val scoreError: String? = null,

        val aiReport: Report? = null,
        val reportError: String? = null
    )

class GeoScoreViewModel(
    private val getGeoScore: GetGeoScore,
    private val getAiReport: GetAiReport
) : ViewModel() {

    private val _uiState = MutableStateFlow(GeoScoreUiState())
    val uiState: StateFlow<GeoScoreUiState> = _uiState.asStateFlow()

    fun load(location: Location) {
        viewModelScope.launch {
            runCatching { getGeoScore.calculateGeoScore(location.lat, location.lon) }
                .fold(
                    onSuccess = { score ->
                        _uiState.update {
                            it.copy(
                                geoScore = score,
                                grade = scoreToGrade(score.geoScore)
                            )
                        }
                        loadReport(score)
                    },
                    onFailure = { e ->
                        _uiState.update {
                            //TODO noen exceptions kastes inne i GeoScore algoritmen sjekk hvordan det funker med denne onFailure funksjonen
                            it.copy(scoreError = e.message ?: "Ukjent feil")
                        }
                    }
                )
        }
    }

    private suspend fun loadReport(score: GeoScore) {
        runCatching {
            getAiReport.generateReport(
                hazardScore = score.hazardScore,
                exposureScore = score.exposureScore,
                vulnerabilityScore = score.vulnerabilityScore,
                grade = scoreToGrade(score.geoScore)
            )
        }.fold(
            onSuccess = { text ->
                _uiState.update { it.copy(aiReport = text) }
            },
            onFailure = { e ->
                _uiState.update { it.copy(reportError = e.message) }
            }
        )
    }
}
