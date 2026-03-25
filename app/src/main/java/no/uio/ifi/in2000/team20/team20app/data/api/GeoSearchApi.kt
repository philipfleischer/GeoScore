package no.uio.ifi.in2000.team20.team20app.data.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team20.team20app.data.model.GeoResponse

interface GeoSearchApiService {
    suspend fun searchLocationWithQuery(query: String, lat: Double? = null, lon: Double? = null): GeoResponse

    suspend fun searchLocationWithCoordinates(lat: Double, lon: Double): GeoResponse
}

object GeoSearchClientProvider {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }

        defaultRequest {
            header(HttpHeaders.Accept, "application/json")
        }
    }
}