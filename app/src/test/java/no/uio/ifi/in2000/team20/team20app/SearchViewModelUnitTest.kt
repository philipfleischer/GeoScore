package no.uio.ifi.in2000.team20.team20app

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team20.team20app.data.repository.GeoSearchRepositoryService
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.domain.model.SearchResult
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.uio_GPT_SearchViewModel
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelUnitTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun searchViewModelTestFirstResult() = runTest {
        //arrang
        val viewModel = uio_GPT_SearchViewModel(FakeGeoSearchRepository(), mainDispatcherRule.testDispatcher)

        //act
        viewModel.updateInput("Oslo")
        advanceUntilIdle() // venter på debounce (300ms) og coroutinen

        //assert
        val firstresult = Location(
            address = "Karl Johans gate 1, Oslo",
            name = "Oslo",
            municipality = "Oslo",
            county = "Oslo",
            lat = 59.9139,
            lon = 10.7522
        )

        assertEquals(firstresult, viewModel.uiState.value.results[0])
    }

    @Test
    fun searchViewModelTestBlankQuery() = runTest {
        val viewModel = uio_GPT_SearchViewModel(FakeGeoSearchRepository(), mainDispatcherRule.testDispatcher)

        viewModel.updateInput("")
        advanceUntilIdle() // venter på debounce (300ms) og coroutinen

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assert(state.results.isEmpty())
        assertNull(state.inputError)

    }

    @Test
    fun searchViewModelTestError() = runTest {
        val viewModel = uio_GPT_SearchViewModel(fakeGeoSearchRepositoryError(), mainDispatcherRule.testDispatcher)

        viewModel.updateInput("Oslo")
        advanceUntilIdle() // venter på debounce (300ms) og coroutinen

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)

    }
}

class FakeGeoSearchRepository : GeoSearchRepositoryService{
    override suspend fun getSearchResults(query: String, lat: Double?, lon: Double?): SearchResult {
        return SearchResult(
            locations = listOf(
                Location(
                    address = "Karl Johans gate 1, Oslo",
                    name = "Oslo",
                    municipality = "Oslo",
                    county = "Oslo",
                    lat = 59.9139,
                    lon = 10.7522
                ),
                Location(
                    address = "Bryggen 1, Bergen",
                    name = "Bergen",
                    municipality = "Bergen",
                    county = "Vestland",
                    lat = 60.3975,
                    lon = 5.3245
                ),
                Location(
                    address = "Nedre Elvehavn 4, Trondheim",
                    name = "Trondheim",
                    municipality = "Trondheim",
                    county = "Trøndelag",
                    lat = 63.4305,
                    lon = 10.3951
                )
            )
        )
    }
}

class fakeGeoSearchRepositoryError : GeoSearchRepositoryService{
    override suspend fun getSearchResults(query: String, lat: Double?, lon: Double?): SearchResult {
        throw Exception("API unavailable")
    }
}
