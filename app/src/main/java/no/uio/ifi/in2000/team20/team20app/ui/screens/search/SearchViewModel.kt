package no.uio.ifi.in2000.team20.team20app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.data.repository.GeoSearchRepositoryService
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.util.Constants.CANCELLED_SEARCH
import no.uio.ifi.in2000.team20.team20app.util.Constants.HTTP_CLIENT_ERROR
import no.uio.ifi.in2000.team20.team20app.util.Constants.HTTP_OK
import no.uio.ifi.in2000.team20.team20app.util.Constants.HTTP_SERVER_ERROR
import no.uio.ifi.in2000.team20.team20app.util.Constants.NO_INTERNET
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: GeoSearchRepositoryService
) : ViewModel() {

    companion object {
        private const val DEBOUNCE_DELAY_MS = 300L
    }

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // Holds the active search job so it can be cancelled when the query changes.
    private var searchJob: Job? = null

    /**
     * Called from the UI whenever the search text changes.
     * Updates the query, sanitizes it, and starts a debounced search.
     */
    fun updateInput(text: String) {

        _uiState.update {
            it.copy(
                query = text,
                isLoading = it.results.isEmpty(), // This way the UI doesn't say "No results" for the split second of debounce
                inputError = null,
                error = null,
            )
        }

        searchJob?.cancel()
        // No dispatcher needed cause the data layer is responsible for threading
        // GeoSearchRepository uses Ktor which is main-safe and handles IO internally
        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_DELAY_MS)
            performSearch(text)
        }
    }

    /**
     * Performs the actual search based on the provided query.
     * Validates blank input and updates state accordingly.
     */
    private suspend fun performSearch(query: String) {
        if (query.isBlank()) {
            setIdleState()
            return
        }

        setLoadingState()

        val result = repository.getSearchResults(query)

        when (result.status) {
            HTTP_OK -> {
                if (result.locations.isEmpty()) {
                    setErrorState("Ingen resultater funnet.")
                }else {
                    setSuccessState(result.locations)
                }
            }
            HTTP_CLIENT_ERROR -> {
                setErrorState("Klient feilet. Prøv igjen.")
            }
            HTTP_SERVER_ERROR -> {
                setErrorState("Server feilet. Prøv igjen.")
            }
            CANCELLED_SEARCH -> {
                // We don't want to restart the loading animation
            }
            NO_INTERNET -> {
                setErrorState("Ingen internet. Koble til å prøv igjen.")
            }
        }
    }

    fun resetQuery() {
        searchJob?.cancel()
        _uiState.update {
            it.copy(
                query = "",
                isLoading = false,
                results = emptyList(),
                error = null,
                inputError = null
            )
        }
    }

    fun addRecentlySearched(location: Location) {
        val updated =
            listOf(location) + _uiState.value.recentlySearched.filter { it != location }
        _uiState.update { it.copy(recentlySearched = updated) }
    }

    private fun setIdleState() {
        _uiState.update {
            it.copy(
                isLoading = false,
                results = emptyList(),
                error = null,
                inputError = null
            )
        }
    }

    private fun setLoadingState() {
        _uiState.update {
            it.copy(
                isLoading = true,
                error = null,
                inputError = null
            )
        }
    }

    private fun setSuccessState(locations: List<Location>) {
        _uiState.update {
            it.copy(
                isLoading = false,
                results = locations,
                error = null,
                inputError = null
            )
        }
    }

    private fun setErrorState(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                error = message,
                inputError = null,
                results = emptyList() // This way the UI doesn't show stale results for a split second when adding a character
            )
        }
    }
}
