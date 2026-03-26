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
    val isLoading: Boolean = false,
    val error: String? = null,                                      // null betyr ingen feil
    )