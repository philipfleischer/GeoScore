package no.uio.ifi.in2000.team20.team20app.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import no.uio.ifi.in2000.team20.team20app.data.api.LocationForecastApiService
import no.uio.ifi.in2000.team20.team20app.data.api.LocationForecastRouts
import no.uio.ifi.in2000.team20.team20app.data.model.WeatherResponseDto

class LocationForecastRemoteDataSource(
    private val client: HttpClient
): LocationForecastApiService {
    override suspend fun getWeatherData(lat: Double, lon: Double): WeatherResponseDto {
        val response = client.get(LocationForecastRouts.GET) {
            parameter("lat", lat)
            parameter("lon", lon)
        }


        if (response.status.value == 200) {
            val body: WeatherResponseDto = response.body() //WeatherresponsDto

            return body

        } else { throw Exception("Locationforecast api not succesfull ${response.status.value} ") }
    }


}