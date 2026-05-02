package no.uio.ifi.in2000.team20.team20app

import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class FrostViewModelTest {
/*
    ===Commented out for testing of the Algorithem===
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadClimateDataWithValidLocationSetsLoadedState() = runTest {
        // Arrange
        val fakeRepo = FakeFrostRepository()
        val viewModel = FrostViewModel(repo = fakeRepo)

        // Act
        viewModel.loadClimateData(lat = 59.91, lon = 10.74)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertNotNull(state.climateData)
    }

    @Test
    fun loadClimateDataWithErrorSetsErrorState() = runTest {
        // Arrange
        val fakeRepo = FakeFrostRepositoryError()
        val viewModel = FrostViewModel(repo = fakeRepo)

        // Act
        viewModel.loadClimateData(lat = 59.91, lon = 10.74)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertNull(state.climateData)
    }
}

// --- Test doubles ---

class FakeFrostRepository : FrostRepositoryService {
    override suspend fun getClimateData(lat: Double, lon: Double): ClimateData {
        // Arrange: return hardcoded climate data
        return ClimateData(
            stationId = "SN18700",
            stationName = "Blindern",
            observations = listOf(
                ClimateObservation(
                    time = "2024-01-01T00:00:00.000Z",
                    airTemperature = 5.2,
                    precipitation = 42.1
                )
            )
        )
    }
}

class FakeFrostRepositoryError : FrostRepositoryService {
    override suspend fun getClimateData(lat: Double, lon: Double): ClimateData {
        throw Exception("Frost API unavailable")
    }

 */
}
