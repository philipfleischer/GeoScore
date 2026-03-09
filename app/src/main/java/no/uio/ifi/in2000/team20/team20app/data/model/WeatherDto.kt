package no.uio.ifi.in2000.team20.team20app.data.model

/**
 * Data Transfer Object for weather/climate API responses.
 *
 * Responsibility:
 * - Represent raw API JSON structure
 *
 * Why:
 * Separates external API format from domain models.
 *
 * * -------Example use--------
 *  * Frost API:
 *  * {
 *  *   "stationId": "SN18700",
 *  *   "time": "2024-05-01T12:00:00Z",
 *  *   "temperature": 12.4,
 *  *   "windSpeed": 5.6,
 *  *   "precipitation": 1.2
 *  * }
 *  *
 *  * Dto:
 *  * data class WeatherDto(
 *  *     val stationId: String,
 *  *     val time: String,
 *  *     val temperature: Double,
 *  *     val windSpeed: Double,
 *  *     val precipitation: Double
 *  * )
 */