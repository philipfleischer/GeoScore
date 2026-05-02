package no.uio.ifi.in2000.team20.team20app

import kotlinx.coroutines.ExperimentalCoroutinesApi

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