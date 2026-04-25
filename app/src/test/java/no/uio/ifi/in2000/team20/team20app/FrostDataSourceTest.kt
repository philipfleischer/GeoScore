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

    /*@Test
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
    }*/

    @Test
    fun getRankedObservationsForPrecipitationReturnsResponse() = runBlocking {
        // Arrange
        val lat = 59.91
        val lon = 10.74

        // Act
        val response = dataSource.getRankedObservationsForPrecipitation(lat = lat, lon = lon)

        // Assert
        assertNotNull(response)
        assertTrue(response.data.tseries.isNotEmpty())
    }

    @Test
    fun getRankedObservationsForPrecipitationHasCorrectElementId() = runBlocking {
        // Arrange
        val lat = 59.91
        val lon = 10.74

        // Act
        val response = dataSource.getRankedObservationsForPrecipitation(lat = lat, lon = lon)

        // Assert
        val elementIds = response.data.tseries.map { it.header.extra.element.id }
        assertTrue(elementIds.all { it == "sum(precipitation_amount P1D)" })
    }

    @Test
    fun getRankedObservationsForWindReturnsResponse() = runBlocking {
        // Arrange
        val lat = 59.91
        val lon = 10.74

        // Act
        val response = dataSource.getRankedObservationsForWind(lat = lat, lon = lon)

        // Assert
        assertNotNull(response)
        assertTrue(response.data.tseries.isNotEmpty())
    }

    @Test
    fun getRankedObservationsForWindHasWindElementId() = runBlocking {
        // Arrange
        val lat = 59.91
        val lon = 10.74

        // Act
        val response = dataSource.getRankedObservationsForWind(lat = lat, lon = lon)

        // Assert — forventer enten gust eller mean wind speed
        val elementIds = response.data.tseries.map { it.header.extra.element.id }.toSet()
        val validIds = setOf("max(wind_speed_of_gust P1D)", "mean(wind_speed P1D)")
        assertTrue(elementIds.all { it in validIds })
    }

    @Test
    fun getRankedObservationsForPrecipitationAllStationsHaveStationId() = runBlocking {
        // Arrange
        val lat = 59.91
        val lon = 10.74

        // Act
        val response = dataSource.getRankedObservationsForPrecipitation(lat = lat, lon = lon)

        // Assert
        val stationIds = response.data.tseries.map { it.header.id.stationid }
        assertTrue(stationIds.all { it > 0 })
    }
}
