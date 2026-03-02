package no.uio.ifi.in2000.team20.team20app.data.repository


/**
 * WeatherRepository
 *
 * What this class is:
 * - A single entry point for all "weather" data used by the app (e.g. current conditions, forecast, wind).
 *
 * What it should do:
 * - Fetch weather data from one or more data sources (network, local cache, mock data).
 * - Convert API/DTO objects into internal models used by the rest of the app.
 * - Provide stable, easy-to-use functions for ViewModels/UseCases, regardless of API details.
 *
 * Why it exists:
 * - Separates concerns: UI code should not care about endpoints, JSON structure, or HTTP errors.
 * - Makes the project easier to maintain, test, and extend (caching, retrying, multiple providers, etc.).
 */
class WeatherRepository(
    // private val weatherDataSource: WeatherDataSource
) {
    // TODO: Add suspend functions like:
    // suspend fun getWeather(location: Location): WeatherInfo
}