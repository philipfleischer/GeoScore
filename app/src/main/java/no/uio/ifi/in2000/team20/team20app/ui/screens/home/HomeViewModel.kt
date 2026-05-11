package no.uio.ifi.in2000.team20.team20app.ui.screens.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for HomeScreen.
 *
 * Responsibility:
 * - Manage home-screen-specific UI state
 *
 * Why:
 * Separates UI from data logic.
 * Climate data is owned by FrostViewModel (shared).
 * Hazard data will be owned by HazardViewModel (shared) once implemented.
 */
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}