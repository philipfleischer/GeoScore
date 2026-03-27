package no.uio.ifi.in2000.team20.team20app.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.data.api.GeoSearchClientProvider
import no.uio.ifi.in2000.team20.team20app.data.datasource.AddressRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.data.datasource.LocationRemoteDatasource
import no.uio.ifi.in2000.team20.team20app.data.repository.GeoSearchRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.Location

class SearchViewModel : ViewModel() {

    private val repository = GeoSearchRepository(
        locationDatasource = LocationRemoteDatasource(
            client = GeoSearchClientProvider.client
        ),
        addressDatasource = AddressRemoteDataSource(
            client = GeoSearchClientProvider.client
        )
    )

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        Log.d("SearchViewModel", "SearchViewModel created")
    }

    // Holder på den aktive søke-jobben slik at vi kan avbryte den hvis brukeren skriver mer.
    private var searchJob: Job? = null

    /*
     * Kalles hver gang søkefeltet endres i UI-en.
     * Kansellerer forrige søk, venter 300ms (debounce), og kaller så API-et.
     * Dette hindrer unødvendige API-kall mens brukeren skriver.
     *
     * MERK: Vi kan gå for debounce + collectLatest som skal være mer idiomatisk Kotlin/Flow
     * begge fungerer likt, men Flow krever @OptIn(FlowPreview::class)
     */
    fun updateInput(text: String) {
        _uiState.update { it.copy(query = text) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(300) //debounce
            // Geonorge-adresse-APIet håndteres med try/catch i GeoSearchRepository, så korte søk er OK
            if (text.isBlank()) {
                _uiState.update { it.copy(isLoading = false, results = emptyList(), error = null) }
                return@launch
            }
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val result = repository.getSearchResults(text)
                _uiState.update { it.copy(isLoading = false, results = result.locations) }
            } catch (e: CancellationException) {
                throw e // let the coroutine framework handle job cancellation normally
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Search failed for query \"$text\": ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, error = "Søk feilet. Prøv igjen.") }
            }
        }
    }

    /*
     * Kalles når brukeren trykker på et søkeresultat.
     * Legger lokasjonen øverst i listen og fjerner eventuelle duplikater.
     * Listen lagres i uiState og vises når søkefeltet er tomt.
     */
    // Tilbakestiller søkefeltet og resultater, f.eks. når man åpner søkeskjermen på nytt
    fun resetQuery() {
        searchJob?.cancel()
        _uiState.update { it.copy(query = "", isLoading = false, results = emptyList(), error = null) }
    }

    fun addRecentlySearched(location: Location) {
        val updated = listOf(location) + _uiState.value.recentlySearched.filter { it != location }
        _uiState.update { it.copy(recentlySearched = updated) }
    }
}