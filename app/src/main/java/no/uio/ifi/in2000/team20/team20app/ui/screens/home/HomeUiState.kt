package no.uio.ifi.in2000.team20.team20app.ui.screens.home

import no.uio.ifi.in2000.team20.team20app.domain.model.ForecastPoint
import no.uio.ifi.in2000.team20.team20app.domain.model.HazardRisk
import no.uio.ifi.in2000.team20.team20app.domain.model.Recommendation

/**
 * UI state holder for HomeScreen.
 *
 * Responsibility:
 * - Represent loading, success, error states
 *
 * Why:
 * Keeps UI reactive and state-driven.
 */
//TODO undersøk om denne br flyttes inn i HomeViewModel.kt?
data class HomeUiState(
    val areaName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,                                      // null betyr ingen feil
    val weather: ForecastPoint? = null,                             // fra LocationForecast API
    val hazardRisks: List<HazardRisk> = emptyList(),                // fra NVE/hazard API (ikke implementert ennå)
    val recommendations: List<Recommendation> = emptyList()         // klimatilpassningsråd (ikke implementert ennå)
)