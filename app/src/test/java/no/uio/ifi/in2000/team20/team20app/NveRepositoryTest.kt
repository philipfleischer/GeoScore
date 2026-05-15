package no.uio.ifi.in2000.team20.team20app

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team20.team20app.data.datasource.NveZonesRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.data.repository.NveZonesRepository
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Integration tests for NveZonesRepository.
 * Makes real API calls to NVE – requires internet connection.
 */
class NveRepositoryTest {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        defaultRequest { header(HttpHeaders.Accept, "application/json") }
    }
    private val dataSource = NveZonesRemoteDataSource(client)
    private val repo = NveZonesRepository(dataSource)

    // Koordinater i flomsone, ikke skredsone
    private val flomLat = 59.9554
    private val flomLon = 11.0488

    // Koordinater i skredsone, ikke flomsone
    private val skredLat = 58.641
    private val skredLon = 7.447

    // Koordinater verken i skred- eller flomsone
    private val ingenLat = 60.7964
    private val ingenLon = 11.0814

    @Test
    fun flomKoordinaterErIFlomsone() = runBlocking {
        assertTrue(repo.isInFloodZone(flomLat, flomLon))
    }

    @Test
    fun flomKoordinaterErIkkeISkredsone() = runBlocking {
        assertFalse(repo.isInLandslideZone(flomLat, flomLon))
    }

    @Test
    fun skredKoordinaterErIkkeSomLandslideZone() = runBlocking {
        assertFalse(repo.isInLandslideZone(skredLat, skredLon))
    }

    @Test
    fun skredKoordinaterErIkkeIFlomsone() = runBlocking {
        assertFalse(repo.isInFloodZone(skredLat, skredLon))
    }

    @Test
    fun ingenSoneKoordinaterErIkkeIFlomsone() = runBlocking {
        assertFalse(repo.isInFloodZone(ingenLat, ingenLon))
    }

    @Test
    fun ingenSoneKoordinaterErIkkeISkredsone() = runBlocking {
        assertFalse(repo.isInLandslideZone(ingenLat, ingenLon))
    }
}
