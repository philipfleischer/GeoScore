package no.uio.ifi.in2000.team20.team20app.data.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team20.team20app.data.model.WeatherResponseDto

interface LocationForecastApiService {


    suspend fun getWeatherData(lat: Double, lon: Double): WeatherResponseDto


}

object LocationForecsastClientProvider {
    val client = HttpClient(CIO){
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

object LocationForecastRouts {
    private const val BASE_URL = "https://in2000.api.met.no/weatherapi/locationforecast/2.0/"
    const val GET = "${BASE_URL}compact"
}