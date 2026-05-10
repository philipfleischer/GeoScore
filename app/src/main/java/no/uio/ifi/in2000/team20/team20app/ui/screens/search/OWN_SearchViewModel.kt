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

class OWN_SearchViewModel (
    private val repository: GeoSearchRepositoryService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {


    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        //Log.d("OWN_SearchViewModel", "OWN_SearchViewModel created")
    }

    // searchJob holds on to the active search job so that it can be canceled if the user is not finished typing.
    private var searchJob: Job? = null

    /*
     * updateInput gets called on every time searchbar is updated in UI
     * While the user is typing, the search gets canceled within 300ms duration (debounce) to avoid
     * new API calls for every character typed for a given location name. 300 ms give the user a chance to
     * finish typing.
     *
     * Alternatively, we may use debounce + collectLatest for more idiomatic Kotlin/Flow,
     * but current method works the same. (Flow requires @OptIn(FlowPreview::class))
     * */
    fun updateInput(text: String) {
        _uiState.update { it.copy(query = text, inputError = null) }

        searchJob?.cancel()

        searchJob = viewModelScope.launch(ioDispatcher) {
            delay(300) // 300 ms debounce window

            // TODO: fix comment -> Geonorge-adresse-APIet håndteres med try/catch i GeoSearchRepository, så korte søk er OK
            if (text.isBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        results = emptyList(),
                        error = null,
                        inputError = null
                    )
                }
                return@launch
            }

            else {
                _uiState.update { it.copy(isLoading = true, error = null, inputError = null) }
                // For non-blank user inputs, sendQueryToRepo is called to fetch search results from repository
                sendQueryToRepo(text)
            }
        }
    }

    suspend fun sendQueryToRepo(query: String){
        try {
            val result = repository.getSearchResults(query)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    results = result.locations
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // For other exceptions, the cause is usually problems with API or network connection
            //Log.e("OWN_SearchViewModel", "Søk feilet: \"$query\": ${e.message}", e)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Søk feilet. Prøv igjen."
                )
            }
        }
    }

    /*
    * Call addRecentlySearch when user clicks on a search result.
    * Location is then added in the head of the list (of recently searched addresses), and duplicates are removed.
    * The list is saved in uiState and shown on the screen only when searchbar is empty.
    *
    * Call resetQuery to reset searchbar, such as when the searchbar is reopened.
    * */
    fun resetQuery() {
        searchJob?.cancel()
        _uiState.update { it.copy(query = "", isLoading = false, results = emptyList(), error = null) }
    }

    fun addRecentlySearched(location: Location) {
        val updated = listOf(location) + _uiState.value.recentlySearched.filter { it != location }
        _uiState.update { it.copy(recentlySearched = updated) }
    }
}