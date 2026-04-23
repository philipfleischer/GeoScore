package no.uio.ifi.in2000.team20.team20app

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team20.team20app.data.datasource.FrostDataSourceService
import no.uio.ifi.in2000.team20.team20app.data.model.FrostObservationResponseDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV0ObservationResponseDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV1AvailableDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV1DataDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV1ElementDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV1ExtraDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV1HeaderDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV1IdDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV1ObservationBodyDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV1ObservationDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV1ResponseDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV1TimeSeriesDto
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FrostRepositoryTest {

    /*
    @Test
    fun getClimateDataWithValidLocationReturnsClimateData() = runBlocking {
        // Arrange
        val fakeDataSource = FakeFrostDataSource()
        val repo = FrostRepository(fakeDataSource)

        // Act
        val result = repo.getFrostStats(lat = 59.91, lon = 10.74)

        // Assert
        assertNotNull(result)
        assertEquals("SN18700", result.stationId)
        assertTrue(result.observations.isNotEmpty())
    }
    */

    @Test
    fun getWindAndPrecipitationObservationsReturnsBothListsNotEmpty() = runBlocking {
        // Arrange
        val fakeDataSource = FakeFrostDataSource()
        val repo = FrostRepository(fakeDataSource)

        // Act
        val result = repo.getWindAndParcipitationObservations(lat = 59.91, lon = 10.74)

        // Assert
        assertNotNull(result)
        assertTrue(result.precipitationValues.isNotEmpty())
        assertTrue(result.windValues.isNotEmpty())
    }

    @Test
    fun getWindAndPrecipitationObservationsFiltersOutZeroValues() = runBlocking {
        // Arrange
        val fakeDataSource = FakeFrostDataSource()
        val repo = FrostRepository(fakeDataSource)

        // Act
        val result = repo.getWindAndParcipitationObservations(lat = 59.91, lon = 10.74)

        // Assert
        assertTrue(result.precipitationValues.all { it > 0.0 })
        assertTrue(result.windValues.all { it > 0.0 })
    }

    @Test
    fun getWindAndPrecipitationObservationsWithErrorThrowsException() {
        // Arrange
        val failingDataSource = FailingFrostDataSource()
        val repo = FrostRepository(failingDataSource)

        // Act & Assert
        try {
            runBlocking { repo.getWindAndParcipitationObservations(lat = 0.0, lon = 0.0) }
            org.junit.Assert.fail("Expected an exception to be thrown")
        } catch (e: Exception) {
            assertNotNull(e)
        }
    }

    /*
    @Test
    fun getClimateDataWithErrorThrowsException() {
        // Arrange
        val failingDataSource = FailingFrostDataSource()
        val repo = FrostRepository(failingDataSource)

        // Act & Assert
        try {
            runBlocking { repo.getClimateData(lat = 0.0, lon = 0.0) }
            org.junit.Assert.fail("Expected an exception to be thrown")
        } catch (e: Exception) {
            assertNotNull(e)
        }
    }*/
}

// --- Test doubles ---

private fun makeFrostV1Response(values: List<String>, elementId: String) = FrostV1ResponseDto(
    data = FrostV1DataDto(
        tseries = values.map { value ->
            FrostV1TimeSeriesDto(
                header = FrostV1HeaderDto(
                    id = FrostV1IdDto(stationid = 18700),
                    extra = FrostV1ExtraDto(element = FrostV1ElementDto(id = elementId)),
                    available = FrostV1AvailableDto(from = "2000-01-01T00:00:00Z")
                ),
                observations = listOf(
                    FrostV1ObservationDto(
                        time = "2000-01-01T00:00:00Z",
                        body = FrostV1ObservationBodyDto(value = value)
                    )
                )
            )
        }
    )
)

class FakeFrostDataSource : FrostDataSourceService {
    override suspend fun getTemperatureNormals(
        lat: Double,
        lon: Double
    ): FrostV0ObservationResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun getPrecipitationNormals(
        lat: Double,
        lon: Double
    ): FrostObservationResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun getPrecipitationMean(
        lat: Double,
        lon: Double
    ): FrostObservationResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun getSnowDepthHistory(
        lat: Double,
        lon: Double
    ): FrostObservationResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun getWindHistory(
        lat: Double,
        lon: Double
    ): FrostObservationResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun getSunshineNormals(
        lat: Double,
        lon: Double
    ): FrostV0ObservationResponseDto {
        TODO("Not yet implemented")
    }


    override suspend fun getRankedObservationsForParcipitation(
        lat: Double, lon: Double, startYear: Int, endYear: Int, maxDist: Double, maxCount: Int
    ): FrostV1ResponseDto = makeFrostV1Response(
        values = listOf("5.2", "0.0", "12.4", "8.1"),
        elementId = "sum(precipitation_amount P1D)"
    )

    override suspend fun getRankedObservationsForWind(
        lat: Double, lon: Double, startYear: Int, endYear: Int, maxDist: Double, maxCount: Int
    ): FrostV1ResponseDto = makeFrostV1Response(
        values = listOf("3.1", "0.0", "9.7", "6.4"),
        elementId = "max(wind_speed_of_gust P1D)"
    )
}

class FailingFrostDataSource : FrostDataSourceService {
    override suspend fun getTemperatureNormals(
        lat: Double,
        lon: Double
    ): FrostV0ObservationResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun getPrecipitationNormals(
        lat: Double,
        lon: Double
    ): FrostObservationResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun getPrecipitationMean(
        lat: Double,
        lon: Double
    ): FrostObservationResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun getSnowDepthHistory(
        lat: Double,
        lon: Double
    ): FrostObservationResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun getWindHistory(
        lat: Double,
        lon: Double
    ): FrostObservationResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun getSunshineNormals(
        lat: Double,
        lon: Double
    ): FrostV0ObservationResponseDto {
        TODO("Not yet implemented")
    }


    override suspend fun getRankedObservationsForParcipitation(
        lat: Double, lon: Double, startYear: Int, endYear: Int, maxDist: Double, maxCount: Int
    ): FrostV1ResponseDto = throw Exception("Network error")

    override suspend fun getRankedObservationsForWind(
        lat: Double, lon: Double, startYear: Int, endYear: Int, maxDist: Double, maxCount: Int
    ): FrostV1ResponseDto = throw Exception("Network error")
}
