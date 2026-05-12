package no.uio.ifi.in2000.team20.team20app

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepositoryService
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.domain.model.WindAndPrecipitationObservationsResult
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FrostViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadFrostStatsWithValidLocationSetsLoadedStateAndData() = runTest {
        // Arrange
        val viewModel = FrostViewModel(FakeFrostRepository())
        val location = Location(
            address = "Test",
            name = "Oslo",
            municipality = "Oslo",
            county = "Oslo",
            lat = 59.91,
            lon = 10.74
        )

        // Act
        viewModel.loadFrostStats(location)
        advanceUntilIdle() // wait for async coroutines to complete

        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.temperatureMean)
        assertNull(state.temperatureError)
    }

    @Test
    fun loadFrostStatsWithErrorSetsErrorStateForTemperature() = runTest {
        // Arrange
        val viewModel = FrostViewModel(FakeFrostRepositoryError())
        val location = Location(
            address = "Test",
            name = "Oslo",
            municipality = "Oslo",
            county = "Oslo",
            lat = 59.91,
            lon = 10.74
        )

        // Act
        viewModel.loadFrostStats(location)
        advanceUntilIdle() // wait for async coroutines to complete

        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.temperatureError)
        assertNull(state.temperatureMean)
    }
}

class FakeFrostRepository : FrostRepositoryService {
    override suspend fun getTemperatureData(lat: Double, lon: Double): Result<Triple<List<Double>, List<Double>, List<Double>>> {
        return Result.success(
            Triple(
                listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0), // mean
                listOf(2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0), // max
                listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0)  // min
            )
        )
    }

    override suspend fun getWindData(lat: Double, lon: Double): Result<Triple<List<Double>, List<Double>, List<Double>>> {
        return Result.success(
            Triple(
                listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0), // mean
                listOf(2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0), // maxSpeed
                listOf(3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0)  // maxGust
            )
        )
    }

    override suspend fun getSunshineData(lat: Double, lon: Double): Result<Triple<List<Double>, String?, Double?>> {
        return Result.success(
            Triple(
                listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0), // hours
                "Test Station", // stationName
                5.0  // distanceKm
            )
        )
    }

    override suspend fun getSnowData(lat: Double, lon: Double): Result<Pair<List<Double>, List<Double>>> {
        return Result.success(
            Pair(
                listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0), // mean
                listOf(2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0)  // max
            )
        )
    }

    override suspend fun getPrecipitationData(lat: Double, lon: Double): Result<Pair<List<Double>, List<Double>>> {
        return Result.success(
            Pair(
                listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0), // rainyDays
                listOf(10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0)  // maxDaily
            )
        )
    }

    override suspend fun getWindAndPrecipitationObservations(lat: Double, lon: Double): WindAndPrecipitationObservationsResult {
        return WindAndPrecipitationObservationsResult(
            precipitationValues = emptyMap(),
            windValues = emptyMap()
        )
    }
}

class FakeFrostRepositoryError : FrostRepositoryService {
    override suspend fun getTemperatureData(lat: Double, lon: Double): Result<Triple<List<Double>, List<Double>, List<Double>>> {
        return Result.failure(Exception("Temperature API unavailable"))
    }

    override suspend fun getWindData(lat: Double, lon: Double): Result<Triple<List<Double>, List<Double>, List<Double>>> {
        return Result.success(
            Triple(
                listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0),
                listOf(2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0),
                listOf(3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0)
            )
        )
    }

    override suspend fun getSunshineData(lat: Double, lon: Double): Result<Triple<List<Double>, String?, Double?>> {
        return Result.success(
            Triple(
                listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0),
                "Test Station",
                5.0
            )
        )
    }

    override suspend fun getSnowData(lat: Double, lon: Double): Result<Pair<List<Double>, List<Double>>> {
        return Result.success(
            Pair(
                listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0),
                listOf(2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0)
            )
        )
    }

    override suspend fun getPrecipitationData(lat: Double, lon: Double): Result<Pair<List<Double>, List<Double>>> {
        return Result.success(
            Pair(
                listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0),
                listOf(10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0)
            )
        )
    }

    override suspend fun getWindAndPrecipitationObservations(lat: Double, lon: Double): WindAndPrecipitationObservationsResult {
        return WindAndPrecipitationObservationsResult(
            precipitationValues = emptyMap(),
            windValues = emptyMap()
        )
    }
}
