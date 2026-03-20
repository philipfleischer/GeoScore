package no.uio.ifi.in2000.team20.team20app

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team20.team20app.BuildConfig.FROST_V0_CLIENT_ID
import no.uio.ifi.in2000.team20.team20app.BuildConfig.FROST_V0_CLIENT_SECRET
import no.uio.ifi.in2000.team20.team20app.data.api.FrostClientProvider
import no.uio.ifi.in2000.team20.team20app.data.datasource.FrostDataSource
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Integration test for FrostDataSource.
 */

class FrostDataSourceTest {

    // Arrange (shared setup)
    // Access credentials from constants generated from local.properties
    private val credentials = "${FROST_V0_CLIENT_ID}:$FROST_V0_CLIENT_SECRET}"
    
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
