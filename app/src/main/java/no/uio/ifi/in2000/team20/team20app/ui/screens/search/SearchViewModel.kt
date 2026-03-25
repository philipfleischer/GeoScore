package no.uio.ifi.in2000.team20.team20app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
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

    /*
     * Kalles hver gang søkefeltet endres.
     * Henter stednavn fra Kartverket og oppdaterer UI-tilstanden.
     */
    fun search(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(results = emptyList(), isLoading = false, error = null) }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val result = repository.getSearchResults(query)
                _uiState.update { it.copy(isLoading = false, results = result.locations) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Søk feilet. Prøv igjen.") }
            }
        }
    }

    fun addRecentlySearched(location: Location) {
        val updated = listOf(location) + _uiState.value.recentlySearched.filter { it != location }
        _uiState.update { it.copy(recentlySearched = updated) }
    }
}
