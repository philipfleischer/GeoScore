package no.uio.ifi.in2000.team20.team20app.data.repository

import no.uio.ifi.in2000.team20.team20app.data.datasource.LocationForecastRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.data.model.InstantDetailsDto
import no.uio.ifi.in2000.team20.team20app.data.model.PropertiesDto
import no.uio.ifi.in2000.team20.team20app.domain.model.ForecastPoint
import no.uio.ifi.in2000.team20.team20app.domain.model.WeatherForecast

interface LocationForecastRepositoryService{
    suspend fun getWeatherForPoint(lat: Double, lon: Double): ForecastPoint
}

class LocationForecastRepository(
    private val dataSource: LocationForecastRemoteDataSource
): LocationForecastRepositoryService {


    private fun InstantDetailsDto.toDomain(time: String): WeatherForecast{
        return WeatherForecast(
            time = time,
            airTemperature = air_temperature,
            relativeHumidity = relative_humidity,
            windSpeed = wind_speed
        )
    }

    private fun PropertiesDto.toDomain(): ForecastPoint {
        val instantWeather = timeseries[0].data.instant?.details?.toDomain("Now") //inneholder det første timeseries objekt

        val futureWeather = timeseries.drop(1).mapNotNull { //dropper time 1 fordi det er lik instant weather
            it.data.instant?.details?.toDomain(it.time) //lager ForecastPeriod elementer
        }

        return ForecastPoint(
            instantWeather = instantWeather,
            futureWeather = futureWeather
        )

    }

    override suspend fun getWeatherForPoint(lat: Double, lon: Double): ForecastPoint {
        val weatherResponse = dataSource.getWeatherData(59.91273, 10.74609)

        //den kaller på TimeseriesDto.toDomain() som igjen kaller på de andre .toDomain funksjonene
        return weatherResponse.properties.toDomain()

    }


}