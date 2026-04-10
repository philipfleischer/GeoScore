package no.uio.ifi.in2000.team20.team20app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.data.repository.GeoSearchRepositoryService
import no.uio.ifi.in2000.team20.team20app.domain.model.Location

class SearchViewModel(
    private val repository: GeoSearchRepositoryService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
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
        val sanitizedText = sanitize(text)

        _uiState.update {
            it.copy(
                query = sanitizedText,
                inputError = null,
                error = null
            )
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch(ioDispatcher) {
            delay(DEBOUNCE_DELAY_MS)
            performSearch(sanitizedText)
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

        try {
            val result = repository.getSearchResults(query)
            setSuccessState(result.locations)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            setErrorState("Søk feilet. Prøv igjen.")
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
                inputError = null
            )
        }
    }

    private fun sanitize(query: String): String {
        return query.trim().replace(Regex("\\s+"), " ")
    }
}
