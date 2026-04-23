package no.uio.ifi.in2000.team20.team20app.ui.screens.home

/**
 * UI state holder for HomeScreen.
 *
 * Responsibility:
 * - Represent home-screen-specific state
 *
 * Why:
 * Keeps UI reactive and state-driven.
 * Climate and hazard data live in their own shared ViewModels.
 */
//TODO undersøk om denne br flyttes inn i HomeViewModel.kt?
data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)