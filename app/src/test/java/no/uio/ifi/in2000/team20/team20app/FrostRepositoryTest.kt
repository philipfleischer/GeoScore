package no.uio.ifi.in2000.team20.team20app

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team20.team20app.data.datasource.FrostDataSourceService
import no.uio.ifi.in2000.team20.team20app.data.model.FrostObservationDataDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostObservationResponseDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostObservationValueDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostSourceDto
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FrostRepositoryTest {

    @Test
    fun getClimateDataWithValidLocationReturnsClimateData() = runBlocking {
        // Arrange
        val fakeDataSource = FakeFrostDataSource()
        val repo = FrostRepository(fakeDataSource)

        // Act
        val result = repo.getClimateData(lat = 59.91, lon = 10.74)

        // Assert
        assertNotNull(result)
        assertEquals("SN18700", result.stationId)
        assertTrue(result.observations.isNotEmpty())
    }

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
    }
}

// --- Test doubles ---

class FakeFrostDataSource : FrostDataSourceService {
    override suspend fun getStation(lat: Double, lon: Double): FrostSourceDto {
        // Arrange: return hardcoded nearest station
        return FrostSourceDto(id = "SN18700", name = "Blindern")
    }

    override suspend fun getObservations(stationId: String): FrostObservationResponseDto {
        // Arrange: return hardcoded observations
        return FrostObservationResponseDto(
            data = listOf(
                FrostObservationDataDto(
                    sourceId = stationId,
                    referenceTime = "2024-01-01T00:00:00.000Z",
                    observations = listOf(
                        FrostObservationValueDto(
                            elementId = "mean(air_temperature P1M)",
                            value = 5.2
                        ),
                        FrostObservationValueDto(
                            elementId = "best_estimate_sum(precipitation_amount P1M)",
                            value = 42.1
                        )
                    )
                )
            )
        )
    }
}

class FailingFrostDataSource : FrostDataSourceService {
    override suspend fun getStation(lat: Double, lon: Double): FrostSourceDto {
        throw Exception("Network error")
    }

    override suspend fun getObservations(stationId: String): FrostObservationResponseDto {
        throw Exception("Network error")
    }
}
