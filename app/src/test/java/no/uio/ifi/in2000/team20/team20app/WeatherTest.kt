package no.uio.ifi.in2000.team20.team20app

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team20.team20app.data.api.LocationForecsastClientProvider
import no.uio.ifi.in2000.team20.team20app.data.datasource.LocationForecastRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.data.repository.LocationForecastRepository
import org.junit.Test

class WeatherApiTest {


    @Test
    fun testWeatherCall() = runBlocking {

        val client = LocationForecsastClientProvider.client

        val dataSource = LocationForecastRemoteDataSource(client)

        val repo = LocationForecastRepository(dataSource)

        val response = repo.getWeatherForPoint(59.91273, 10.74609)

        println(response.toString())
    }


}