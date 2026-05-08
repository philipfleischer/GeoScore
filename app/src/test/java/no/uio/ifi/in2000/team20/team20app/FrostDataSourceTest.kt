package no.uio.ifi.in2000.team20.team20app

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team20.team20app.data.datasource.FrostDataSource
import no.uio.ifi.in2000.team20.team20app.util.Constants
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Integration test for FrostDataSource.
 * Credentials are read from Constants (backed by BuildConfig / local.properties).
 */

class FrostDataSourceTest {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
            json(Json { ignoreUnknownKeys = true }, contentType = ContentType.Text.Plain)
        }
        expectSuccess = false
        defaultRequest { header("User-Agent", "IN2000-Team20 jeryosa@uio.no") }
    }

    private val dataSource = FrostDataSource(client)

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
