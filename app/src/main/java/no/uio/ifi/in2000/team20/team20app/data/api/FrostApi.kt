package no.uio.ifi.in2000.team20.team20app.data.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Interface for communicating with the Frost API (climate data).
 *
 * This file defines the network endpoints used to fetch
 * temperature, precipitation, wind and climate statistics.
 *
 * Responsibility:
 * - Define API calls
 * - No business logic
 *
 * Why:
 * Keeps external API communication separated from the rest of the app.
 */

object FrostClientProvider {
    val client = HttpClient(CIO) {
        // Frost returns JSON error bodies with content-type: text/plain — register both
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
            json(Json { ignoreUnknownKeys = true }, contentType = ContentType.Text.Plain)
        }
        // Do not throw on non-2xx — datasource checks status and throws explicitly
        expectSuccess = false
        defaultRequest {
            header("User-Agent", "IN2000-Team20 jeryosa@uio.no")
        }
    }
}

object FrostRoutes {
    // V1 - frost-rc.met.no: has current/recent observations, lacks historical aggregates
    const val OBSERVATIONS_V1 = "https://frost-rc.met.no/api/v1/obs/ranked/get"
    // V0 - frost.met.no: has historical normals and monthly aggregates
    const val OBSERVATIONS_V0 = "https://frost.met.no/observations/v0.jsonld"
    const val SOURCES_V0 = "https://frost.met.no/sources/v0.jsonld"
    const val AVAILABLE_TIMESERIES_V0 = "https://frost.met.no/observations/availableTimeSeries/v0.jsonld"
}