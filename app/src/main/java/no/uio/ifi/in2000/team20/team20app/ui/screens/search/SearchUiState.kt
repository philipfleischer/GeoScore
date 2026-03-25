package no.uio.ifi.in2000.team20.team20app.ui.screens.search

import no.uio.ifi.in2000.team20.team20app.domain.model.Location

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<Location> = emptyList(),
    val error: String? = null,
    val recentlySearched: List<Location> = emptyList()
)
