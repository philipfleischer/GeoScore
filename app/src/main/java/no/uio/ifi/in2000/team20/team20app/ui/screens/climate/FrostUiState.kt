package no.uio.ifi.in2000.team20.team20app.ui.screens.climate

import no.uio.ifi.in2000.team20.team20app.domain.model.ClimateData

/**
 * UI state holder for climate/Frost data.
 *
 * Responsibility:
 * - Represent loading, success, and error states for Frost API data
 *
 * Why:
 * Keeps the UI reactive and state-driven.
 */
data class FrostUiState(
    val isLoading: Boolean = false,
    val error: String? = null,        // null means no error
    val climateData: ClimateData? = null
)
