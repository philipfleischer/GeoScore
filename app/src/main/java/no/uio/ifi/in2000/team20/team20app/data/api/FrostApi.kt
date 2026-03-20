package no.uio.ifi.in2000.team20.team20app.data.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
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
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
        defaultRequest {
            header("User-Agent", "IN2000-Team20 jeryosa@uio.no")
        }
    }
}

object FrostRoutes {
    private const val BASE_URL = "https://frost.met.no"
    const val SOURCES = "$BASE_URL/sources/v0.jsonld"
    const val OBSERVATIONS = "$BASE_URL/observations/v0.jsonld"
}