package no.uio.ifi.in2000.team20.team20app

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team20.team20app.data.api.FrostClientProvider
import no.uio.ifi.in2000.team20.team20app.data.datasource.FrostDataSource
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Integration test for FrostDataSource.
 */
/*
class FrostDataSourceTest {

    // Arrange (shared setup)
    // TODO: These credentials should not be hardcoded here.
    //  David had an idea for secrets
    private val credentials = "clientId:clientSecret"
    private val dataSource = FrostDataSource(
        client = FrostClientProvider.client,
        credentials = credentials
    )

    @Test
    fun getStationWithCoordinatesReturnsNearestStation() = runBlocking {
        // Arrange
        val lat = 59.91
        val lon = 10.74

        // Act
        val station = dataSource.getStation(lat = lat, lon = lon)

        // Assert
        assertNotNull(station)
        assertTrue(station.id.startsWith("SN"))
    }

    @Test
    fun getObservationsWithValidStationIdReturnsObservationList() = runBlocking {
        // Arrange
        val station = dataSource.getStation(lat = 59.91, lon = 10.74)

        // Act
        val response = dataSource.getObservations(stationId = station.id)

        // Assert
        assertNotNull(response)
        assertTrue(response.data.isNotEmpty())
    }
}


 */