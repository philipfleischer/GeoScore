package no.uio.ifi.in2000.team20.team20app.data.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


object NveZonesClientProvider {
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


object NveRoutes {
    private const val BASE = "https://kart.nve.no/enterprise/rest/services"

    const val FLOM = "$BASE/Flomaktsomhet/MapServer/1/query"

    const val SKRED = "$BASE/Skredfaresoner1/MapServer/3/query"

}