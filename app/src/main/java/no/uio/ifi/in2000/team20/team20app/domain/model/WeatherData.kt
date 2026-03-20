package no.uio.ifi.in2000.team20.team20app.domain.model

/**
 * Domain model representing processed weather data.
 *
 * Responsibility:
 * - Clean representation used by UI
 *
 * Why:
 * Decouples UI from raw API structure.
 */
data class ForecastPoint(
    val instantWeather: WeatherForecast?,
    val futureWeather: List<WeatherForecast>
)

data class WeatherForecast(
    val time: String,
    val airTemperature: Float,
    val relativeHumidity: Float,
    val windSpeed: Float,
)

data class ForecastPeriod(
    val airTemperature: Float,
    val relativeHumidity: Float,
    val windSpeed: Float,
)