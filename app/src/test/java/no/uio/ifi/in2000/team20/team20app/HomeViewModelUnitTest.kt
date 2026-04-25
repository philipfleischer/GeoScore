package no.uio.ifi.in2000.team20.team20app

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeViewModel
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelUnitTest {
    /*
    === Commented out for testing of the algorithem ===

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadClimateDataWithValidLocationSetsLoadedState() = runTest {
        val viewModel = HomeViewModel(frostRepository = FakeFrostRepository())
        val location = Location(
            address = "Blindern, Oslo",
            name = "Oslo",
            municipality = "Oslo",
            county = "Oslo",
            lat = 59.91,
            lon = 10.74
        )

        viewModel.loadClimateData(location)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertNotNull(state.climateData)
    }

    @Test
    fun loadClimateDataWithErrorSetsErrorState() = runTest {
        val viewModel = HomeViewModel(frostRepository = FakeFrostRepositoryError())
        val location = Location(
            address = "Blindern, Oslo",
            name = "Oslo",
            municipality = "Oslo",
            county = "Oslo",
            lat = 59.91,
            lon = 10.74
        )

        viewModel.loadClimateData(location)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertNull(state.climateData)
    }

     */
}